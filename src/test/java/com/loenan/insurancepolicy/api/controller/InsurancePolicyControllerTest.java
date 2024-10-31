package com.loenan.insurancepolicy.api.controller;

import com.loenan.insurancepolicy.api.model.EditInsurancePolicyDto;
import com.loenan.insurancepolicy.api.model.ErrorDto;
import com.loenan.insurancepolicy.api.model.FieldErrorDto;
import com.loenan.insurancepolicy.api.model.InsurancePolicyDto;
import com.loenan.insurancepolicy.domain.contract.exception.InsurancePolicyNotFoundException;
import com.loenan.insurancepolicy.domain.contract.exception.InvalidInputException;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicyStatus;
import com.loenan.insurancepolicy.domain.core.validation.ValidationUtil;
import com.loenan.insurancepolicy.infrastructure.persistence.model.entity.InsurancePolicyEntity;
import com.loenan.insurancepolicy.infrastructure.persistence.repository.InsurancePolicyRepository;
import com.loenan.insurancepolicy.test.util.AbstractDatabaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.loenan.insurancepolicy.domain.contract.model.InsurancePolicyStatus.ACTIVE;
import static com.loenan.insurancepolicy.domain.contract.model.InsurancePolicyStatus.INACTIVE;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InsurancePolicyControllerTest extends AbstractDatabaseTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private InsurancePolicyRepository repository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        repository.deleteAll();
    }

    // region Get all insurance policies

    @Test
    void shouldGetZeroAllInsurancePolicies_whenNoPolicyExists() {
        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/v1/insurance-policies")
            .then()
            .statusCode(200)
            .extract().body().jsonPath();

        List<InsurancePolicyDto> dtos = jsonPath.getList("", InsurancePolicyDto.class);
        assertThat(dtos).isEmpty();
    }

    @Test
    void shouldGetAllInsurancePolicies_whenPolicesExist() {
        InsurancePolicyEntity policy1 = saveNumberedPolicy(1, ACTIVE);
        InsurancePolicyEntity policy2 = saveNumberedPolicy(2, INACTIVE);

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/v1/insurance-policies")
            .then()
            .statusCode(200)
            .extract().body().jsonPath();

        List<InsurancePolicyDto> dtos = jsonPath.getList("", InsurancePolicyDto.class);
        assertThat(dtos).hasSize(2);
        assertDtoIsMatchingExpectedEntity(dtos.get(0), policy1);
        assertDtoIsMatchingExpectedEntity(dtos.get(1), policy2);
    }

    // endregion

    // region Get an insurance policy by ID

    @Test
    void shouldGetInsurancePolicy_whenPolicyExists() {
        saveNumberedPolicy(1, ACTIVE);
        InsurancePolicyEntity policy2 = saveNumberedPolicy(2, INACTIVE);
        saveNumberedPolicy(3, ACTIVE);

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/v1/insurance-policies/" + policy2.getId())
            .then()
            .statusCode(200)
            .extract().body().jsonPath();

        InsurancePolicyDto dto = jsonPath.getObject("", InsurancePolicyDto.class);
        assertDtoIsMatchingExpectedEntity(dto, policy2);
    }

    @Test
    void shouldReceive404NotFound_whenPolicyDoesNotExist() {
        saveNumberedPolicy(1, ACTIVE);
        saveNumberedPolicy(2, INACTIVE);
        saveNumberedPolicy(3, ACTIVE);

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/v1/insurance-policies/666")
            .then()
            .statusCode(404)
            .extract().body().jsonPath();

        ErrorDto dto = jsonPath.getObject("", ErrorDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(ErrorDto::key)
            .isEqualTo(InsurancePolicyNotFoundException.KEY);
    }

    // endregion

    // region Create an insurance policy

    @Test
    void shouldCreateInsurancePolicy_whenPolicyIsValid() {
        EditInsurancePolicyDto requestDto = buildNumberedEditInsurancePolicyDto(4, "ACTIVE");

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .when()
            .post("/v1/insurance-policies")
            .then()
            .statusCode(200)
            .extract().body().jsonPath();

        InsurancePolicyDto dto = jsonPath.getObject("", InsurancePolicyDto.class);
        assertDtoIsMatchingRequestDto(dto, requestDto);
        assertDtoExistsInDatabaseWithId(dto.id(), requestDto);
    }

    @Test
    void shouldReceive400BadRequestWithValidationErrors_whenPolicyIsInvalid() {
        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body(new EditInsurancePolicyDto(null, null, null, null))
            .when()
            .post("/v1/insurance-policies")
            .then()
            .statusCode(400)
            .extract().body().jsonPath();

        ErrorDto dto = jsonPath.getObject("", ErrorDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(ErrorDto::key)
            .isEqualTo(InvalidInputException.KEY);
        assertThat(dto.fieldErrors())
            .hasSize(4)
            .containsExactlyInAnyOrder(
                Stream.of("name", "status", "startDate", "endDate")
                    .map(fieldName -> new FieldErrorDto(fieldName, ValidationUtil.NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted(fieldName)))
                    .toArray(FieldErrorDto[]::new)
            );
        assertThat(repository.findAll()).isEmpty(); // nothing should be created
    }

    @Test
    void shouldReceive400BadRequestWithValidationError_whenPolicyStatusIsInvalid() {
        EditInsurancePolicyDto requestDto = buildNumberedEditInsurancePolicyDto(4, "XXX");

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .when()
            .post("/v1/insurance-policies")
            .then()
            .statusCode(400)
            .extract().body().jsonPath();

        ErrorDto dto = jsonPath.getObject("", ErrorDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(ErrorDto::key)
            .isEqualTo(InvalidInputException.KEY);
        assertThat(dto.fieldErrors())
            .hasSize(1)
            .containsExactly(new FieldErrorDto("status",
                ValidationUtil.INVALID_ENUM_VALUE_ERROR_MESSAGE.formatted("status", "ACTIVE, INACTIVE")));

        assertThat(repository.findAll()).isEmpty(); // nothing should be created
    }

    @Test
    void shouldReceiveInvalidFormatError_whenCreateMessageIsInvalid() {
        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body("{\"startDate\": \"not a date\"}")
            .when()
            .post("/v1/insurance-policies")
            .then()
            .statusCode(400)
            .extract().body().jsonPath();

        ErrorDto dto = jsonPath.getObject("", ErrorDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(ErrorDto::key)
            .isEqualTo("invalid_format");
    }

    // endregion

    // region Edit an insurance policy

    @Test
    void shouldEditInsurancePolicy_whenPolicyExistsAndEditedDataIsValid() {
        InsurancePolicyEntity policy1 = saveNumberedPolicy(1, ACTIVE);
        EditInsurancePolicyDto requestDto = buildNumberedEditInsurancePolicyDto(2, "INACTIVE");

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .when()
            .put("/v1/insurance-policies/" + policy1.getId())
            .then()
            .statusCode(200)
            .extract().body().jsonPath();

        InsurancePolicyDto dto = jsonPath.getObject("", InsurancePolicyDto.class);
        assertDtoIsMatchingRequestDto(dto, requestDto);
        assertDtoExistsInDatabaseWithId(policy1.getId(), requestDto);
    }

    @Test
    void shouldReceive404NotFound_whenEditedPolicyDoesNotExist() {
        EditInsurancePolicyDto requestDto = buildNumberedEditInsurancePolicyDto(2, "INACTIVE");

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body(requestDto)
            .when()
            .put("/v1/insurance-policies/153")
            .then()
            .statusCode(404)
            .extract().body().jsonPath();

        ErrorDto dto = jsonPath.getObject("", ErrorDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(ErrorDto::key)
            .isEqualTo(InsurancePolicyNotFoundException.KEY);
    }

    @Test
    void shouldReceive400BadRequestWithValidationErrors_whenEditedPolicyDataIsInvalid() {
        InsurancePolicyEntity policy1 = saveNumberedPolicy(1, ACTIVE);

        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body(new EditInsurancePolicyDto(null, null, null, null))
            .when()
            .put("/v1/insurance-policies/" + policy1.getId())
            .then()
            .statusCode(400)
            .extract().body().jsonPath();

        ErrorDto dto = jsonPath.getObject("", ErrorDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(ErrorDto::key)
            .isEqualTo(InvalidInputException.KEY);
        assertThat(dto.fieldErrors())
            .hasSize(4)
            .containsExactlyInAnyOrder(
                Stream.of("name", "status", "startDate", "endDate")
                    .map(fieldName -> new FieldErrorDto(fieldName, ValidationUtil.NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted(fieldName)))
                    .toArray(FieldErrorDto[]::new)
            );
        // stored entity should be unchanged
        Optional<InsurancePolicyEntity> storedPolicy = repository.findById(policy1.getId());
        assertThat(storedPolicy)
            .isPresent()
            .get()
            .usingRecursiveComparison(RecursiveComparisonConfiguration.builder()
                .withComparatorForFields(getZonedDateTimeComparator(), "creation", "lastUpdate")
                .build())
            .isEqualTo(policy1);
    }

    @Test
    void shouldReceiveInvalidFormatError_whenEditMessageIsInvalid() {
        JsonPath jsonPath = given()
            .contentType(ContentType.JSON)
            .body("{\"startDate\": \"not a date\"}")
            .when()
            .put("/v1/insurance-policies/153")
            .then()
            .statusCode(400)
            .extract().body().jsonPath();

        ErrorDto dto = jsonPath.getObject("", ErrorDto.class);
        assertThat(dto)
            .isNotNull()
            .extracting(ErrorDto::key)
            .isEqualTo("invalid_format");
    }

    // endregion

    // region Utility methods

    private InsurancePolicyEntity saveNumberedPolicy(int number, InsurancePolicyStatus status) {
        return savePolicy(
            "My policy " + number,
            status,
            LocalDate.of(2020 + number, 1, 1),
            LocalDate.of(2020 + number, 12, 31)
        );
    }

    private InsurancePolicyEntity savePolicy(
        String name,
        InsurancePolicyStatus status,
        LocalDate startDate,
        LocalDate endDate
    ) {
        InsurancePolicyEntity entity = new InsurancePolicyEntity();
        entity.setName(name);
        entity.setStatus(status);
        entity.setStartDate(startDate);
        entity.setEndDate(endDate);
        return repository.save(entity);
    }

    private EditInsurancePolicyDto buildNumberedEditInsurancePolicyDto(int number, String status) {
        return new EditInsurancePolicyDto(
            "My policy " + number,
            status,
            LocalDate.of(2020 + number, 1, 1),
            LocalDate.of(2020 + number, 12, 31)
        );
    }

    private void assertDtoIsMatchingExpectedEntity(InsurancePolicyDto dto, InsurancePolicyEntity entity) {
        assertThat(dto)
            .isNotNull()
            .extracting(
                InsurancePolicyDto::id,
                InsurancePolicyDto::name,
                InsurancePolicyDto::status,
                InsurancePolicyDto::startDate,
                InsurancePolicyDto::endDate
            )
            .containsExactly(
                entity.getId(),
                entity.getName(),
                entity.getStatus(),
                entity.getStartDate(),
                entity.getEndDate()
            );
    }

    private void assertDtoIsMatchingRequestDto(InsurancePolicyDto dto, EditInsurancePolicyDto requestDto) {
        assertThat(dto)
            .isNotNull()
            .extracting(
                InsurancePolicyDto::name,
                insurancePolicyDto -> insurancePolicyDto.status().name(),
                InsurancePolicyDto::startDate,
                InsurancePolicyDto::endDate
            )
            .containsExactly(
                requestDto.name(),
                requestDto.status(),
                requestDto.startDate(),
                requestDto.endDate()
            );
    }

    private void assertDtoExistsInDatabaseWithId(Integer id, EditInsurancePolicyDto dto) {
        assertThat(repository.findById(id))
            .isPresent()
            .get()
            .extracting(
                InsurancePolicyEntity::getName,
                entity -> entity.getStatus().name(),
                InsurancePolicyEntity::getStartDate,
                InsurancePolicyEntity::getEndDate
            )
            .containsExactly(
                dto.name(),
                dto.status(),
                dto.startDate(),
                dto.endDate()
            );
    }

    private static Comparator<ZonedDateTime> getZonedDateTimeComparator() {
        return Comparator.comparing(ZonedDateTime::toInstant);
    }

    // endregion
}
