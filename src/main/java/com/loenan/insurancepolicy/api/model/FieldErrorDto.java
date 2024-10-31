package com.loenan.insurancepolicy.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "Error on a field")
public record FieldErrorDto(
    @Schema(title = "Field name", example = "status")
    String fieldName,
    @Schema(title = "Error message", example = "The field is mandatory")
    String message
) {
}
