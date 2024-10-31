package com.loenan.insurancepolicy.api.controller;

import com.loenan.insurancepolicy.api.mapper.InsurancePolicyApiMapper;
import com.loenan.insurancepolicy.api.model.FieldErrorDto;
import com.loenan.insurancepolicy.api.model.ErrorDto;
import com.loenan.insurancepolicy.domain.contract.exception.UserInputException;
import com.loenan.insurancepolicy.domain.contract.model.error.ErrorType;
import com.loenan.insurancepolicy.domain.contract.model.error.HasFieldErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class InsurancePolicyExceptionHandler {

    private final InsurancePolicyApiMapper mapper;

    @Autowired
    public InsurancePolicyExceptionHandler(InsurancePolicyApiMapper mapper) {
        this.mapper = mapper;
    }

    @ExceptionHandler(UserInputException.class)
    public ResponseEntity<ErrorDto> onUserInputException(UserInputException ex) {
        return new ResponseEntity<>(
            new ErrorDto(ex.getFunctionalKey(), ex.getMessage(), mapToFieldErrorDtos(ex)),
            mapToHttpStatusCode(ex.getErrorType())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> onHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(
            new ErrorDto("invalid_format", ex.getMessage(), Collections.emptyList()),
            HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> onGeneralException(Exception ex) {
        return new ResponseEntity<>(
            new ErrorDto("internal_error", ex.getMessage(), Collections.emptyList()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private HttpStatusCode mapToHttpStatusCode(ErrorType errorType) {
        return switch (errorType) {
            case INVALID_DATA -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND_DATA -> HttpStatus.NOT_FOUND;
        };
    }

    private List<FieldErrorDto> mapToFieldErrorDtos(UserInputException ex) {
        return switch (ex) {
            case HasFieldErrors hasFieldErrors -> mapper.toDtos(hasFieldErrors.getFieldErrors());
            default -> Collections.emptyList();
        };
    }
}
