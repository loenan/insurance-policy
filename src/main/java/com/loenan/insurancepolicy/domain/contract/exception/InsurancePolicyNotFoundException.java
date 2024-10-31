package com.loenan.insurancepolicy.domain.contract.exception;

import com.loenan.insurancepolicy.domain.contract.model.error.ErrorType;

public class InsurancePolicyNotFoundException extends UserInputException {

    public static final String KEY = "insurance_policy_not_found";

    private static final String MESSAGE = "The insurance policy with id %s cannot be found";

    public InsurancePolicyNotFoundException(Integer insurancePolicyId) {
        super(ErrorType.NOT_FOUND_DATA, KEY, MESSAGE.formatted(insurancePolicyId));
    }
}
