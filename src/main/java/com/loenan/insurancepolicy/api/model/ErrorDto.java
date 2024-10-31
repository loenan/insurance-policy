package com.loenan.insurancepolicy.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "Validation error payload")
public record ErrorDto(
    @Schema(title = "Key identifying the error type", example = "invalid_input")
    String key,
    @Schema(title = "Error message", example = "Payload is not valid")
    String message,
    @Schema(title = "Fields with errors")
    List<FieldErrorDto> fieldErrors
) {
}
