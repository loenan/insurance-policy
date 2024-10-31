package com.loenan.insurancepolicy.domain.core.usecase.query;

import com.loenan.insurancepolicy.domain.contract.command.query.GetInsurancePolicyByIdQuery;
import com.loenan.insurancepolicy.domain.contract.exception.InsurancePolicyNotFoundException;
import com.loenan.insurancepolicy.domain.contract.exception.UserInputException;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.port.secondary.InsurancePolicyPersistencePort;
import com.loenan.insurancepolicy.domain.core.usecase.UseCaseHandler;
import com.loenan.insurancepolicy.domain.core.validation.InsurancePolicyFields;
import com.loenan.insurancepolicy.domain.core.validation.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetInsurancePolicyByIdUseCase extends UseCaseHandler<GetInsurancePolicyByIdQuery, InsurancePolicy> {

    private final InsurancePolicyPersistencePort insurancePolicyPersistencePort;

    @Autowired
    public GetInsurancePolicyByIdUseCase(InsurancePolicyPersistencePort insurancePolicyPersistencePort) {
        this.insurancePolicyPersistencePort = insurancePolicyPersistencePort;
    }

    @Override
    protected void validate(GetInsurancePolicyByIdQuery query) throws UserInputException {
        ValidationUtil.validateInput(
            ValidationUtil.validateMandatoryValue(query.id(), InsurancePolicyFields.ID)
        );
    }

    @Override
    protected InsurancePolicy process(GetInsurancePolicyByIdQuery query) {
        return insurancePolicyPersistencePort.getById(query.id())
            .orElseThrow((() -> new InsurancePolicyNotFoundException(query.id())));
    }
}
