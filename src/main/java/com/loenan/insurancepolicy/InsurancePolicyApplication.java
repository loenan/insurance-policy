package com.loenan.insurancepolicy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;

@SpringBootApplication(exclude = LiquibaseAutoConfiguration.class)
public class InsurancePolicyApplication {

    public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(InsurancePolicyApplication.class, args);
    }
}
