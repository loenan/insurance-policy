package com.loenan.insurancepolicy.domain.core.usecase;

import com.loenan.insurancepolicy.domain.contract.exception.UserInputException;
import jakarta.transaction.Transactional;

/**
 * Base class for use case handler.
 * It takes an input and returns a result.
 * This class enforces the pattern to validate the input before processing it.
 *
 * @param <T> The input type
 * @param <R> The result type (use Void to return nothing)
 */
public abstract class UseCaseHandler<T, R> {

    @Transactional
    public R execute(T input) throws UserInputException {
        validate(input);
        return process(input);
    }

    protected abstract void validate(T input) throws UserInputException;

    protected abstract R process(T input);
}
