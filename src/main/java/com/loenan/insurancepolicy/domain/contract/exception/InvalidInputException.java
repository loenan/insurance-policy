package com.loenan.insurancepolicy.domain.contract.exception;

import com.loenan.insurancepolicy.domain.contract.model.error.ErrorType;
import com.loenan.insurancepolicy.domain.contract.model.error.FieldError;
import com.loenan.insurancepolicy.domain.contract.model.error.HasFieldErrors;

import java.util.Collections;
import java.util.List;

public class InvalidInputException extends UserInputException implements HasFieldErrors {

    public static final String KEY = "invalid_input";

    private static final String MESSAGE = "Invalid input received: %d error(s) encountered";

    private final List<FieldError> fieldErrors;

    public InvalidInputException(List<FieldError> fieldErrors) {
        super(ErrorType.INVALID_DATA, KEY, MESSAGE.formatted(fieldErrors.size()));
        this.fieldErrors = Collections.unmodifiableList(fieldErrors);
    }

    @Override
    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }
}
