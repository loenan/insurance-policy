package com.loenan.insurancepolicy.infrastructure.persistence.adapter;

import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.port.secondary.InsurancePolicyPersistencePort;
import com.loenan.insurancepolicy.infrastructure.persistence.model.entity.InsurancePolicyEntity;
import com.loenan.insurancepolicy.infrastructure.persistence.model.mapper.InsurancePolicyPersistenceMapper;
import com.loenan.insurancepolicy.infrastructure.persistence.repository.InsurancePolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InsurancePolicyPersistenceAdapter implements InsurancePolicyPersistencePort {

    private final InsurancePolicyRepository repository;
    private final InsurancePolicyPersistenceMapper mapper;

    @Autowired
    public InsurancePolicyPersistenceAdapter(
        InsurancePolicyRepository repository,
        InsurancePolicyPersistenceMapper mapper
    ) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<InsurancePolicy> getById(Integer id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }

    @Override
    public List<InsurancePolicy> getAll() {
        return repository.findAllByOrderByName()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public InsurancePolicy save(InsurancePolicy insurancePolicy) {
        InsurancePolicyEntity entityToSave = mapper.toEntity(insurancePolicy);
        InsurancePolicyEntity savedEntity = repository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }
}
