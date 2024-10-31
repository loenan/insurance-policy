package com.loenan.insurancepolicy.domain.contract.command.action;

import java.time.LocalDate;

public record EditInsurancePolicyAction(
    Integer id,
    String name,
    String status,
    LocalDate startDate,
    LocalDate endDate
) {
}
