package com.loenan.insurancepolicy.domain.contract.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public record InsurancePolicy(
    Integer id,
    String name,
    InsurancePolicyStatus status,
    LocalDate startDate,
    LocalDate endDate,
    ZonedDateTime creation,
    ZonedDateTime lastUpdate
) {
}
