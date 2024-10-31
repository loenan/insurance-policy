package com.loenan.insurancepolicy.domain.core.adapter;

import com.loenan.insurancepolicy.domain.contract.command.action.CreateInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.action.EditInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.query.GetAllInsurancePoliciesQuery;
import com.loenan.insurancepolicy.domain.contract.command.query.GetInsurancePolicyByIdQuery;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.port.primary.InsurancePolicyPort;
import com.loenan.insurancepolicy.domain.core.usecase.action.CreateInsurancePolicyUseCase;
import com.loenan.insurancepolicy.domain.core.usecase.action.EditInsurancePolicyUseCase;
import com.loenan.insurancepolicy.domain.core.usecase.query.GetAllInsurancePoliciesUseCase;
import com.loenan.insurancepolicy.domain.core.usecase.query.GetInsurancePolicyByIdUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsurancePolicyAdapter implements InsurancePolicyPort {

    private final GetAllInsurancePoliciesUseCase getAllInsurancePoliciesUseCase;
    private final GetInsurancePolicyByIdUseCase getInsurancePolicyByIdUseCase;
    private final CreateInsurancePolicyUseCase createInsurancePolicyUseCase;
    private final EditInsurancePolicyUseCase editInsurancePolicyUseCase;

    @Autowired
    public InsurancePolicyAdapter(
        GetAllInsurancePoliciesUseCase getAllInsurancePoliciesUseCase,
        GetInsurancePolicyByIdUseCase getInsurancePolicyByIdUseCase,
        CreateInsurancePolicyUseCase createInsurancePolicyUseCase,
        EditInsurancePolicyUseCase editInsurancePolicyUseCase
    ) {
        this.getAllInsurancePoliciesUseCase = getAllInsurancePoliciesUseCase;
        this.getInsurancePolicyByIdUseCase = getInsurancePolicyByIdUseCase;
        this.createInsurancePolicyUseCase = createInsurancePolicyUseCase;
        this.editInsurancePolicyUseCase = editInsurancePolicyUseCase;
    }

    @Override
    public List<InsurancePolicy> getAllInsurancePolicies(GetAllInsurancePoliciesQuery query) {
        return getAllInsurancePoliciesUseCase.execute(query);
    }

    @Override
    public InsurancePolicy getInsurancePolicyById(GetInsurancePolicyByIdQuery query) {
        return getInsurancePolicyByIdUseCase.execute(query);
    }

    @Override
    public InsurancePolicy createInsurancePolicy(CreateInsurancePolicyAction action) {
        return createInsurancePolicyUseCase.execute(action);
    }

    @Override
    public InsurancePolicy editInsurancePolicy(EditInsurancePolicyAction action) {
        return editInsurancePolicyUseCase.execute(action);
    }
}
