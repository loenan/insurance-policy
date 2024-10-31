package com.loenan.insurancepolicy.domain.contract.port.primary;

import com.loenan.insurancepolicy.domain.contract.command.action.CreateInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.action.EditInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.query.GetAllInsurancePoliciesQuery;
import com.loenan.insurancepolicy.domain.contract.command.query.GetInsurancePolicyByIdQuery;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;

import java.util.List;

public interface InsurancePolicyPort {

    List<InsurancePolicy> getAllInsurancePolicies(GetAllInsurancePoliciesQuery query);

    InsurancePolicy getInsurancePolicyById(GetInsurancePolicyByIdQuery query);

    InsurancePolicy createInsurancePolicy(CreateInsurancePolicyAction action);

    InsurancePolicy editInsurancePolicy(EditInsurancePolicyAction action);
}
