package com.loenan.insurancepolicy.api.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(title = "Insurance policy edition payload")
public record EditInsurancePolicyDto(
    @Schema(title = "Name of the policy", example = "My policy name")
    String name,
    @Schema(title = "Status of the policy", allowableValues = {"ACTIVE", "INACTIVE"}, example = "ACTIVE")
    String status,
    @Schema(title = "Start date of cover", example = "2024-01-01")
    LocalDate startDate,
    @Schema(title = "End date of cover", example = "2024-12-31")
    LocalDate endDate
) {
}
