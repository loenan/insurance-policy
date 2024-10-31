package com.loenan.insurancepolicy.domain.core.usecase.query;

import com.loenan.insurancepolicy.domain.contract.command.query.GetAllInsurancePoliciesQuery;
import com.loenan.insurancepolicy.domain.contract.exception.UserInputException;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.port.secondary.InsurancePolicyPersistencePort;
import com.loenan.insurancepolicy.domain.core.usecase.UseCaseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllInsurancePoliciesUseCase extends UseCaseHandler<GetAllInsurancePoliciesQuery, List<InsurancePolicy>> {

    private final InsurancePolicyPersistencePort insurancePolicyPersistencePort;

    @Autowired
    public GetAllInsurancePoliciesUseCase(InsurancePolicyPersistencePort insurancePolicyPersistencePort) {
        this.insurancePolicyPersistencePort = insurancePolicyPersistencePort;
    }

    @Override
    protected void validate(GetAllInsurancePoliciesQuery query) throws UserInputException {
        // nothing to validate here
    }

    @Override
    protected List<InsurancePolicy> process(GetAllInsurancePoliciesQuery query) {
        return insurancePolicyPersistencePort.getAll();
    }
}
