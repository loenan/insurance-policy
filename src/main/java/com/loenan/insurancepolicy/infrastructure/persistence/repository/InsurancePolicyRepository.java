package com.loenan.insurancepolicy.infrastructure.persistence.repository;

import com.loenan.insurancepolicy.infrastructure.persistence.model.entity.InsurancePolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicyEntity, Integer> {

    Stream<InsurancePolicyEntity> findAllByOrderByName();
}
