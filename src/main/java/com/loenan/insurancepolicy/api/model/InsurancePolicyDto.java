package com.loenan.insurancepolicy.api.model;

import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(title = "Insurance policy")
public record InsurancePolicyDto(
    @Schema(title = "Identifier of the policy", example = "1234")
    Integer id,
    @Schema(title = "Name of the policy", example = "My policy name")
    String name,
    @Schema(title = "Status of the policy", allowableValues = {"ACTIVE", "INACTIVE"}, example = "ACTIVE")
    InsurancePolicyStatus status,
    @Schema(title = "Start date of cover", example = "2024-01-01")
    LocalDate startDate,
    @Schema(title = "End date of cover", example = "2024-12-31")
    LocalDate endDate,
    @Schema(title = "Date of creation", example = "2024-10-28")
    LocalDate creation,
    @Schema(title = "Date of last update", example = "2024-10-28")
    LocalDate lastUpdate
) {
}
