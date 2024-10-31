package com.loenan.insurancepolicy.domain.core.usecase.action;

import com.loenan.insurancepolicy.domain.contract.command.action.CreateInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.exception.InvalidInputException;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicyStatus;
import com.loenan.insurancepolicy.domain.contract.model.error.FieldError;
import com.loenan.insurancepolicy.domain.contract.port.secondary.InsurancePolicyPersistencePort;
import com.loenan.insurancepolicy.domain.core.model.mapper.InsurancePolicyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.loenan.insurancepolicy.domain.core.validation.InsurancePolicyFields.NAME_MAX_LENGTH;
import static com.loenan.insurancepolicy.domain.core.validation.ValidationUtil.BLANK_STRING_PROVIDED_ERROR_MESSAGE;
import static com.loenan.insurancepolicy.domain.core.validation.ValidationUtil.INVALID_DATE_ORDER_ERROR_MESSAGE;
import static com.loenan.insurancepolicy.domain.core.validation.ValidationUtil.INVALID_ENUM_VALUE_ERROR_MESSAGE;
import static com.loenan.insurancepolicy.domain.core.validation.ValidationUtil.NO_VALUE_PROVIDED_ERROR_MESSAGE;
import static com.loenan.insurancepolicy.domain.core.validation.ValidationUtil.STRING_VALUE_TOO_LONG_ERROR_MESSAGE;
import static com.loenan.insurancepolicy.test.util.TestUtil.assertDateTimeIsCloseToNow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateInsurancePolicyUseCaseTest {

    private static final String NAME = "new policy";
    private static final String VERY_LONG_NAME = "very ".repeat(1000) + "long name";
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 12, 31);

    @Mock
    private InsurancePolicyPersistencePort persistencePort;

    @Mock
    private InsurancePolicy savedInsurancePolicy;

    private InsurancePolicyMapper mapper = Mappers.getMapper(InsurancePolicyMapper.class);

    private CreateInsurancePolicyUseCase useCase;

    @Captor
    private ArgumentCaptor<InsurancePolicy> insurancePolicyCaptor;

    @BeforeEach
    void setUp() {
        useCase = new CreateInsurancePolicyUseCase(persistencePort, mapper);
    }

    @Test
    void shouldSavePolicy_whenDataIsValid() {
        // given
        CreateInsurancePolicyAction action = new CreateInsurancePolicyAction(
            NAME,
            "ACTIVE",
            START_DATE,
            END_DATE
        );
        when(persistencePort.save(any())).thenReturn(savedInsurancePolicy);

        // when
        InsurancePolicy result = useCase.execute(action);

        // then
        verify(persistencePort).save(insurancePolicyCaptor.capture());
        assertThat(insurancePolicyCaptor.getValue())
            .isNotNull()
            .extracting(
                InsurancePolicy::id,
                InsurancePolicy::name,
                InsurancePolicy::status,
                InsurancePolicy::startDate,
                InsurancePolicy::endDate
            )
            .containsExactly(
                null,
                NAME,
                InsurancePolicyStatus.ACTIVE,
                START_DATE,
                END_DATE
            );
        assertDateTimeIsCloseToNow(insurancePolicyCaptor.getValue().creation());
        assertDateTimeIsCloseToNow(insurancePolicyCaptor.getValue().lastUpdate());

        assertThat(result).isSameAs(savedInsurancePolicy);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void shouldThrowInvalidInputException_whenDataIsInvalid(
        String testName,
        CreateInsurancePolicyAction action,
        List<FieldError> expectedFieldErrors
    ) {
        assertThatThrownBy(() -> useCase.execute(action))
            .isInstanceOfSatisfying(InvalidInputException.class,
                ex -> assertThat(ex.getFieldErrors())
                    .containsExactlyInAnyOrder(expectedFieldErrors.toArray(FieldError[]::new)));

        verifyNoInteractions(persistencePort);
    }

    private static Stream<Arguments> provideInvalidData() {
        return Stream.of(
            Arguments.of(
                "Null name",
                new CreateInsurancePolicyAction(null, "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("name")))
            ),
            Arguments.of(
                "Blank name",
                new CreateInsurancePolicyAction("  ", "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", BLANK_STRING_PROVIDED_ERROR_MESSAGE.formatted("name")))
            ),
            Arguments.of(
                "Too long name",
                new CreateInsurancePolicyAction(VERY_LONG_NAME, "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", STRING_VALUE_TOO_LONG_ERROR_MESSAGE.formatted("name", NAME_MAX_LENGTH)))
            ),
            Arguments.of(
                "Null name",
                new CreateInsurancePolicyAction(null, "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("name")))
            ),
            Arguments.of(
                "Null status",
                new CreateInsurancePolicyAction(NAME, null, START_DATE, END_DATE),
                List.of(new FieldError("status", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("status")))
            ),
            Arguments.of(
                "Invalid status",
                new CreateInsurancePolicyAction(NAME, "XXX", START_DATE, END_DATE),
                List.of(new FieldError("status", INVALID_ENUM_VALUE_ERROR_MESSAGE.formatted("status", "ACTIVE, INACTIVE")))
            ),
            Arguments.of(
                "Null start date",
                new CreateInsurancePolicyAction(NAME, "ACTIVE", null, END_DATE),
                List.of(new FieldError("startDate", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("startDate")))
            ),
            Arguments.of(
                "Null end date",
                new CreateInsurancePolicyAction(NAME, "ACTIVE", START_DATE, null),
                List.of(new FieldError("endDate", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("endDate")))
            ),
            Arguments.of(
                "Date order",
                new CreateInsurancePolicyAction(NAME, "ACTIVE", END_DATE, START_DATE),
                List.of(new FieldError("endDate", INVALID_DATE_ORDER_ERROR_MESSAGE.formatted("endDate", "startDate")))
            )
        );
    }
}
