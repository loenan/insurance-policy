package com.loenan.insurancepolicy.domain.core.usecase.action;

import com.loenan.insurancepolicy.domain.contract.command.action.EditInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.exception.InsurancePolicyNotFoundException;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
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
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EditInsurancePolicyUseCaseTest {

    private static final Integer ID = 1234;
    private static final String INITIAL_NAME = "initial name";
    private static final LocalDate INITIAL_START_DATE = LocalDate.of(2023, 1, 1);
    private static final LocalDate INITIAL_END_DATE = LocalDate.of(2023, 12, 31);
    private static final ZonedDateTime INITIAL_CREATION =
        ZonedDateTime.of(2023, 10, 28, 10, 32, 0, 0, ZoneId.of("UTC"));
    private static final String NAME = "edited name";
    private static final String VERY_LONG_NAME = "very ".repeat(1000) + "long name";
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2024, 12, 31);

    @Mock
    private InsurancePolicyPersistencePort persistencePort;

    @Mock
    private InsurancePolicy savedInsurancePolicy;

    private InsurancePolicyMapper mapper = Mappers.getMapper(InsurancePolicyMapper.class);

    private EditInsurancePolicyUseCase useCase;

    @Captor
    private ArgumentCaptor<InsurancePolicy> insurancePolicyCaptor;

    @BeforeEach
    void setUp() {
        useCase = new EditInsurancePolicyUseCase(persistencePort, mapper);
    }

    @Test
    void shouldSavePolicy_whenDataIsValidAndPolicyExists() {
        // given
        EditInsurancePolicyAction action = new EditInsurancePolicyAction(
            ID,
            NAME,
            "ACTIVE",
            START_DATE,
            END_DATE
        );
        when(persistencePort.getById(ID)).thenReturn(Optional.of(new InsurancePolicy(
            ID,
            INITIAL_NAME,
            InsurancePolicyStatus.INACTIVE,
            INITIAL_START_DATE,
            INITIAL_END_DATE,
            INITIAL_CREATION,
            INITIAL_CREATION
        )));
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
                InsurancePolicy::endDate,
                InsurancePolicy::creation
            )
            .containsExactly(
                ID,
                NAME,
                InsurancePolicyStatus.ACTIVE,
                START_DATE,
                END_DATE,
                INITIAL_CREATION
            );
        assertDateTimeIsCloseToNow(insurancePolicyCaptor.getValue().lastUpdate());

        assertThat(result).isSameAs(savedInsurancePolicy);
    }

    @Test
    void shouldThrowNotFoundException_whenPolicyDoesNotExist() {
        // given
        EditInsurancePolicyAction action = new EditInsurancePolicyAction(
            ID,
            NAME,
            "ACTIVE",
            START_DATE,
            END_DATE
        );
        when(persistencePort.getById(ID)).thenReturn(Optional.empty());

        assertThatThrownBy(
            // when
            () -> useCase.execute(action)
        )
            // then
            .isInstanceOf(InsurancePolicyNotFoundException.class);

        verifyNoMoreInteractions(persistencePort);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidData")
    void shouldThrowInvalidInputException_whenDataIsInvalid(
        String testName,
        EditInsurancePolicyAction action,
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
                "Null ID",
                new EditInsurancePolicyAction(null, NAME, "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("id", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("id")))
            ),
            Arguments.of(
                "Null name",
                new EditInsurancePolicyAction(ID, null, "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("name")))
            ),
            Arguments.of(
                "Blank name",
                new EditInsurancePolicyAction(ID, "  ", "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", BLANK_STRING_PROVIDED_ERROR_MESSAGE.formatted("name")))
            ),
            Arguments.of(
                "Too long name",
                new EditInsurancePolicyAction(ID, VERY_LONG_NAME, "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", STRING_VALUE_TOO_LONG_ERROR_MESSAGE.formatted("name", NAME_MAX_LENGTH)))
            ),
            Arguments.of(
                "Null name",
                new EditInsurancePolicyAction(ID, null, "ACTIVE", START_DATE, END_DATE),
                List.of(new FieldError("name", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("name")))
            ),
            Arguments.of(
                "Null status",
                new EditInsurancePolicyAction(ID, NAME, null, START_DATE, END_DATE),
                List.of(new FieldError("status", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("status")))
            ),
            Arguments.of(
                "Invalid status",
                new EditInsurancePolicyAction(ID, NAME, "XXX", START_DATE, END_DATE),
                List.of(new FieldError("status", INVALID_ENUM_VALUE_ERROR_MESSAGE.formatted("status", "ACTIVE, INACTIVE")))
            ),
            Arguments.of(
                "Null start date",
                new EditInsurancePolicyAction(ID, NAME, "ACTIVE", null, END_DATE),
                List.of(new FieldError("startDate", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("startDate")))
            ),
            Arguments.of(
                "Null end date",
                new EditInsurancePolicyAction(ID, NAME, "ACTIVE", START_DATE, null),
                List.of(new FieldError("endDate", NO_VALUE_PROVIDED_ERROR_MESSAGE.formatted("endDate")))
            ),
            Arguments.of(
                "Date order",
                new EditInsurancePolicyAction(ID, NAME, "ACTIVE", END_DATE, START_DATE),
                List.of(new FieldError("endDate", INVALID_DATE_ORDER_ERROR_MESSAGE.formatted("endDate", "startDate")))
            )
        );
    }
}
