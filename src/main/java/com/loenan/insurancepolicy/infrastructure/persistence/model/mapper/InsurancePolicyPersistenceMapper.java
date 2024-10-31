package com.loenan.insurancepolicy.infrastructure.persistence.model.mapper;

import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.infrastructure.persistence.model.entity.InsurancePolicyEntity;
import org.mapstruct.Mapper;

@Mapper
public interface InsurancePolicyPersistenceMapper {

    InsurancePolicy toDomain(InsurancePolicyEntity entity);

    InsurancePolicyEntity toEntity(InsurancePolicy insurancePolicy);
}
