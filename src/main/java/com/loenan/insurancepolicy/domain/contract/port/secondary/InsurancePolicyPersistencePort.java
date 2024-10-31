package com.loenan.insurancepolicy.domain.contract.port.secondary;

import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;

import java.util.List;
import java.util.Optional;

public interface InsurancePolicyPersistencePort {

    Optional<InsurancePolicy> getById(Integer id);

    List<InsurancePolicy> getAll();

    InsurancePolicy save(InsurancePolicy insurancePolicy);
}
