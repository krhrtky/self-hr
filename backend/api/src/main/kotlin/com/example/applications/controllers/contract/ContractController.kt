package com.example.applications.controllers.contract

import app.selfhr.domains.contract.ContractApplicationService
import app.selfhr.domains.contract.ContractApplicationService.BillingConditionDTO
import app.selfhr.domains.contract.ContractApplicationService.ContractCreateDTO
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class ContractController(
    val contractApplicationService: ContractApplicationService,
) {
    @PostMapping("/proprietors/{proprietorId}/contracts")
    fun createContract(
        @PathVariable proprietorId: String,
        @RequestBody requestParams: CreateContractRequest,
    ): CreateContractResponse =
        requestParams
            .mapToCreateDto(proprietorId)
            .let(contractApplicationService::create)
            .let(ContractApplicationService.ContractDTO::mapToResponse)
}

private fun CreateContractRequest.mapToCreateDto(proprietorId: String): ContractCreateDTO = ContractCreateDTO(
    proprietorId = proprietorId,
    number = number,
    contractStartDate = contractStartDate,
    contractEndDate = contractEndDate,
    billingConditions = billingConditions.map {
        BillingConditionDTO(
            type = it.type,
            value = it.value,
        )
    },
    note = note,
)

private fun ContractApplicationService.ContractDTO.mapToResponse(): CreateContractResponse =
    CreateContractResponse(
        id = id,
        number = number,
        contractStartDate = contractStartDate,
        contractEndDate = contractEndDate,
        billingConditions = billingConditions.map {
            BillingCondition(
                type = it.type,
                value = it.value,
            )
        },
        note = note,
    )

data class CreateContractRequest(
    val number: String,
    val contractStartDate: LocalDate,
    val contractEndDate: LocalDate,
    val billingConditions: List<BillingCondition>,
    val note: String?,
)

data class BillingCondition(
    val type: String,
    val value: Double,
)

data class CreateContractResponse(
    val id: String,
    val number: String,
    val contractStartDate: LocalDate,
    val contractEndDate: LocalDate,
    val billingConditions: List<BillingCondition>,
    val note: String?,
)
