package com.loenan.insurancepolicy.domain.contract.model.error;

public record FieldError(
    String fieldName,
    String message
) {
}
