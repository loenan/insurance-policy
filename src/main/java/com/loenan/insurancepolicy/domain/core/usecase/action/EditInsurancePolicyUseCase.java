package com.loenan.insurancepolicy.domain.core.usecase.action;

import com.loenan.insurancepolicy.domain.contract.command.action.EditInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.exception.InsurancePolicyNotFoundException;
import com.loenan.insurancepolicy.domain.contract.exception.UserInputException;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicyStatus;
import com.loenan.insurancepolicy.domain.contract.port.secondary.InsurancePolicyPersistencePort;
import com.loenan.insurancepolicy.domain.core.model.mapper.InsurancePolicyMapper;
import com.loenan.insurancepolicy.domain.core.usecase.UseCaseHandler;
import com.loenan.insurancepolicy.domain.core.validation.InsurancePolicyFields;
import com.loenan.insurancepolicy.domain.core.validation.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EditInsurancePolicyUseCase extends UseCaseHandler<EditInsurancePolicyAction, InsurancePolicy> {

    private final InsurancePolicyPersistencePort insurancePolicyPersistencePort;
    private final InsurancePolicyMapper insurancePolicyMapper;

    @Autowired
    public EditInsurancePolicyUseCase(InsurancePolicyPersistencePort insurancePolicyPersistencePort, InsurancePolicyMapper insurancePolicyMapper) {
        this.insurancePolicyPersistencePort = insurancePolicyPersistencePort;
        this.insurancePolicyMapper = insurancePolicyMapper;
    }

    @Override
    protected void validate(EditInsurancePolicyAction action) throws UserInputException {
        ValidationUtil.validateInput(
            ValidationUtil.validateMandatoryValue(action.id(), InsurancePolicyFields.ID),
            ValidationUtil.validateMandatoryNotBlankString(action.name(), InsurancePolicyFields.NAME),
            ValidationUtil.validateStringLength(action.name(), InsurancePolicyFields.NAME_MAX_LENGTH, InsurancePolicyFields.NAME),
            ValidationUtil.validateMandatoryValue(action.status(), InsurancePolicyFields.STATUS),
            ValidationUtil.validateEnumValue(action.status(), InsurancePolicyStatus.class, InsurancePolicyFields.STATUS),
            ValidationUtil.validateMandatoryValue(action.startDate(), InsurancePolicyFields.START_DATE),
            ValidationUtil.validateMandatoryValue(action.endDate(), InsurancePolicyFields.END_DATE),
            ValidationUtil.validateDateOrder(action.startDate(), action.endDate(), InsurancePolicyFields.START_DATE, InsurancePolicyFields.END_DATE)
        );
    }

    @Override
    protected InsurancePolicy process(EditInsurancePolicyAction action) {
        InsurancePolicy storedInsurancePolicy = insurancePolicyPersistencePort.getById(action.id())
            .orElseThrow(() -> new InsurancePolicyNotFoundException(action.id()));

        InsurancePolicy insurancePolicyToSave = insurancePolicyMapper.toDomain(storedInsurancePolicy, action);
        return insurancePolicyPersistencePort.save(insurancePolicyToSave);
    }
}
