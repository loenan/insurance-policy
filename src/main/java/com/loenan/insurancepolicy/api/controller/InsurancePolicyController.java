package com.loenan.insurancepolicy.api.controller;

import com.loenan.insurancepolicy.api.mapper.InsurancePolicyApiMapper;
import com.loenan.insurancepolicy.api.model.EditInsurancePolicyDto;
import com.loenan.insurancepolicy.api.model.InsurancePolicyDto;
import com.loenan.insurancepolicy.api.model.ErrorDto;
import com.loenan.insurancepolicy.domain.contract.command.action.CreateInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.action.EditInsurancePolicyAction;
import com.loenan.insurancepolicy.domain.contract.command.query.GetAllInsurancePoliciesQuery;
import com.loenan.insurancepolicy.domain.contract.command.query.GetInsurancePolicyByIdQuery;
import com.loenan.insurancepolicy.domain.contract.model.InsurancePolicy;
import com.loenan.insurancepolicy.domain.contract.port.primary.InsurancePolicyPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/insurance-policies")
@Tag(name = "Insurance policy API")
public class InsurancePolicyController {

    private final InsurancePolicyPort insurancePolicyPort;
    private final InsurancePolicyApiMapper mapper;

    @Autowired
    public InsurancePolicyController(InsurancePolicyPort insurancePolicyPort, InsurancePolicyApiMapper mapper) {
        this.insurancePolicyPort = insurancePolicyPort;
        this.mapper = mapper;
    }

    @GetMapping()
    @Operation(summary = "Get all the insurance policies")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The list of all insurance policies found",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = InsurancePolicyDto.class)), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public List<InsurancePolicyDto> getAllInsurancePolicies() {
        return insurancePolicyPort.getAllInsurancePolicies(new GetAllInsurancePoliciesQuery()).stream()
            .map(mapper::toDto)
            .toList();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an insurance policy by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The insurance policy found",
            content = @Content(schema = @Schema(implementation = InsurancePolicyDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "The insurance policy is not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public InsurancePolicyDto getInsurancePolicyById(
        @PathVariable Integer id
    ) {
        InsurancePolicy insurancePolicy = insurancePolicyPort.getInsurancePolicyById(new GetInsurancePolicyByIdQuery(id));
        return mapper.toDto(insurancePolicy);
    }

    @PostMapping()
    @Operation(summary = "Create an insurance policy")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The insurance policy is created",
            content = @Content(schema = @Schema(implementation = InsurancePolicyDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "The payload is not valid",
            content = @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public InsurancePolicyDto createInsurancePolicy(
        @RequestBody EditInsurancePolicyDto insurancePolicyDto
    ) {
        CreateInsurancePolicyAction action = mapper.toCreateAction(insurancePolicyDto);
        InsurancePolicy createdInsurancePolicy = insurancePolicyPort.createInsurancePolicy(action);
        return mapper.toDto(createdInsurancePolicy);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit an insurance policy")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "The insurance policy is updated",
            content = @Content(schema = @Schema(implementation = InsurancePolicyDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "400", description = "The payload is not valid",
            content = @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE)),
        @ApiResponse(responseCode = "404", description = "The insurance policy is not found",
            content = @Content(schema = @Schema(implementation = ErrorDto.class), mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public InsurancePolicyDto editInsurancePolicy(
        @PathVariable Integer id,
        @RequestBody EditInsurancePolicyDto insurancePolicyDto
    ) {
        EditInsurancePolicyAction action = mapper.toEditAction(id, insurancePolicyDto);
        InsurancePolicy editedInsurancePolicy = insurancePolicyPort.editInsurancePolicy(action);
        return mapper.toDto(editedInsurancePolicy);
    }
}
