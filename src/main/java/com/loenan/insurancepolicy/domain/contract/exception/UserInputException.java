package com.loenan.insurancepolicy.domain.contract.exception;

import com.loenan.insurancepolicy.domain.contract.model.error.ErrorType;

/**
 * Base exception thrown when the user sends invalid data.
 */
public abstract class UserInputException extends RuntimeException {

    private final ErrorType errorType;
    private final String functionalKey;

    protected UserInputException(ErrorType errorType, String functionalKey, String message) {
        super(message);
        this.errorType = errorType;
        this.functionalKey = functionalKey;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public String getFunctionalKey() {
        return functionalKey;
    }
}
