package com.loenan.insurancepolicy.domain.core.usecase.query;

import com.loenan.insurancepolicy.domain.contract.command.query.GetAllInsurancePoliciesQuery;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.port.secondary.InsurancePolicyPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllInsurancePoliciesUseCaseTest {

    @Mock
    private InsurancePolicyPersistencePort persistencePort;

    @Mock
    private InsurancePolicy storedInsurancePolicy1;

    @Mock
    private InsurancePolicy storedInsurancePolicy2;

    private GetAllInsurancePoliciesUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetAllInsurancePoliciesUseCase(persistencePort);
    }

    @Test
    void shouldGetAllPolicies_whenPoliciesExists() {
        // given
        when(persistencePort.getAll()).thenReturn(List.of(storedInsurancePolicy1, storedInsurancePolicy2));
        GetAllInsurancePoliciesQuery query = new GetAllInsurancePoliciesQuery();

        // when
        List<InsurancePolicy> result = useCase.execute(query);

        // then
        assertThat(result).containsExactly(storedInsurancePolicy1, storedInsurancePolicy2);
    }

    @Test
    void shouldGetNoPolicies_whenNoPoliciesExists() {
        // given
        when(persistencePort.getAll()).thenReturn(Collections.emptyList());
        GetAllInsurancePoliciesQuery query = new GetAllInsurancePoliciesQuery();

        // when
        List<InsurancePolicy> result = useCase.execute(query);

        // then
        assertThat(result).isEmpty();
    }

}
