package com.loenan.insurancepolicy.domain.core.model.mapper;

import com.loenan.insurancepolicy.domain.contract.command.action.CreateInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.action.EditInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InsurancePolicyMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creation", expression = "java(ZonedDateTime.now())")
    @Mapping(target = "lastUpdate", expression = "java(ZonedDateTime.now())")
    InsurancePolicy toDomain(CreateInsurancePolicyAction action);

    @Mapping(target = "id", source = "initial.id")
    @Mapping(target = "name", source = "action.name")
    @Mapping(target = "status", source = "action.status")
    @Mapping(target = "startDate", source = "action.startDate")
    @Mapping(target = "endDate", source = "action.endDate")
    @Mapping(target = "lastUpdate", expression = "java(ZonedDateTime.now())")
    InsurancePolicy toDomain(InsurancePolicy initial, EditInsurancePolicyAction action);


}
