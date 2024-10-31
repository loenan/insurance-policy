package com.loenan.insurancepolicy.test.util;

import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.Scope;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.ui.LoggerUIService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.DriverManager;
import java.util.Map;

public class AbstractDatabaseTest {
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:17.0-alpine"
    ).withInitScript("db/init-test/init.sql");

    @BeforeAll
    static void beforeAll() throws Exception {
        postgres.start();

        try {
            try (
                var c = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
                var resourceAccessor =  new ClassLoaderResourceAccessor();
                var liquibase = new Liquibase("db/liquibase/master.xml", resourceAccessor, new JdbcConnection(c))
            ) {
                Scope.enter(Map.of(Scope.Attr.ui.name(), new LoggerUIService()));
                liquibase.update(new Contexts());
            }
        } catch (Exception e) {
            postgres.stop();
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

}
