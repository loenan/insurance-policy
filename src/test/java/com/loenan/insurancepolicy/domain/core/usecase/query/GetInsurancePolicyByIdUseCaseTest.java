package com.loenan.insurancepolicy.domain.core.usecase.query;

import com.loenan.insurancepolicy.domain.contract.command.query.GetInsurancePolicyByIdQuery;
import com.loenan.insurancepolicy.domain.contract.exception.InsurancePolicyNotFoundException;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.port.secondary.InsurancePolicyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetInsurancePolicyByIdUseCaseTest {

    private static final Integer ID = 789;

    @Mock
    private InsurancePolicyPersistencePort persistencePort;

    @Mock
    private InsurancePolicy storedInsurancePolicy;

    private GetInsurancePolicyByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetInsurancePolicyByIdUseCase(persistencePort);
    }

    @Test
    void shouldGetPolicy_whenPolicyExists() {
        // given
        when(persistencePort.getById(ID)).thenReturn(Optional.of(storedInsurancePolicy));
        GetInsurancePolicyByIdQuery query = new GetInsurancePolicyByIdQuery(ID);

        // when
        InsurancePolicy result = useCase.execute(query);

        // then
        assertThat(result).isSameAs(storedInsurancePolicy);
    }

    @Test
    void shouldThrowNotFoundException_whenPolicyDoesNotExist() {
        // given
        when(persistencePort.getById(ID)).thenReturn(Optional.empty());
        GetInsurancePolicyByIdQuery query = new GetInsurancePolicyByIdQuery(ID);

        assertThatThrownBy(
            // when
            () -> useCase.execute(query)
        )
            // then
            .isInstanceOf(InsurancePolicyNotFoundException.class);
    }
}
