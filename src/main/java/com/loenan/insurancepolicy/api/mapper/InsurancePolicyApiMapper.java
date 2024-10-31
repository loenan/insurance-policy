package com.loenan.insurancepolicy.api.mapper;

import com.loenan.insurancepolicy.api.model.EditInsurancePolicyDto;
import com.loenan.insurancepolicy.api.model.FieldErrorDto;
import com.loenan.insurancepolicy.api.model.InsurancePolicyDto;
import com.loenan.insurancepolicy.domain.contract.command.action.CreateInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.action.EditInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.model.error.FieldError;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface InsurancePolicyApiMapper {

    CreateInsurancePolicyAction toCreateAction(EditInsurancePolicyDto dto);

    EditInsurancePolicyAction toEditAction(Integer id, EditInsurancePolicyDto dto);

    InsurancePolicyDto toDto(InsurancePolicy insurancePolicy);

    List<FieldErrorDto> toDtos(List<FieldError> fieldErrors);
}
