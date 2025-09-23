package app.selfhr.domains.contract

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.entities.ContractFactory
import app.selfhr.domains.contract.exceptions.ContractException
import app.selfhr.domains.contract.exceptions.ContractNoDuprecatedException
import app.selfhr.domains.contract.exceptions.ContractNotFoundException
import app.selfhr.domains.contract.vo.ContractNo
import app.selfhr.domains.contract.vo.ContractPeriod
import app.selfhr.domains.contract.vo.ContractStatus
import app.selfhr.domains.contract.vo.FixedRateCondition
import app.selfhr.domains.contract.vo.TimeBasedCondition
import app.selfhr.domains.proprietor.vo.ProprietorIDGenerator
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ContractApplicationService(
    private val contractFactory: ContractFactory,
    private val contractRepository: ContractRepository,
    private val proprietorIDGenerator: ProprietorIDGenerator,
) {
    fun create(contractCreateDTO: ContractCreateDTO): Result<ContractDTO, ContractException> {
        contractCreateDTO.number
            .let(::ContractNo)
            .let(contractRepository::find)
            ?.let {
                return Err(ContractNoDuprecatedException("ContractNo(${contractCreateDTO.number}) is already exists"))
            }

        return contractCreateDTO
            .mapToCreateCommand()
            .let(contractFactory::create)
            .apply(contractRepository::save)
            .mapToDTO()
            .let(::Ok)
    }

    fun update(contractUpdateDTO: ContractUpdateDTO) =
        ContractNo(contractUpdateDTO.number)
            .let(contractRepository::find)
            ?.let {
                contractFactory
                    .update(it, contractUpdateDTO.mapToUpdateCommand())
            }
            ?.apply(contractRepository::save)
            ?.mapToDTO()
            ?: throw ContractNotFoundException("ContractNo(${contractUpdateDTO.number}) not found")

    data class ContractCreateDTO(
        val number: String,
        val proprietorId: String,
        val contractStartDate: LocalDate,
        val contractEndDate: LocalDate,
        val billingConditions: List<BillingConditionDTO>,
        val note: String?,
    )

    data class ContractUpdateDTO(
        val number: String,
        val contractStartDate: LocalDate,
        val contractEndDate: LocalDate,
        val status: String,
        val billingConditions: List<BillingConditionDTO>,
        val note: String?,
    )

    data class BillingConditionDTO(
        val type: String,
        val value: Double,
    )

    data class ContractDTO(
        val id: String,
        val number: String,
        val contractStartDate: LocalDate,
        val contractEndDate: LocalDate,
        val status: String,
        val billingConditions: List<BillingConditionDTO>,
        val note: String?,
    )

    @Suppress("ThrowingExceptionsWithoutMessageOrCause")
    private fun ContractCreateDTO.mapToCreateCommand(): ContractFactory.ContractCreateCommand {
        val billingConditions = this.billingConditions.map {
            when (it.type) {
                "TimeBasedCondition" -> TimeBasedCondition(it.value)
                "FixedRateCondition" -> FixedRateCondition(it.value)
                else -> throw IllegalArgumentException()
            }
        }
        return ContractFactory.ContractCreateCommand(
            number = this.number.let(::ContractNo),
            proprietorId = proprietorIDGenerator.from(this.proprietorId),
            period = ContractPeriod(
                contractStartDate,
                contractEndDate,
            ),
            billingConditions = billingConditions,
            note = this.note,
        )
    }
}

private fun ContractApplicationService.ContractUpdateDTO.mapToUpdateCommand(): ContractFactory.ContractUpdateCommand {
    val billingConditions = this.billingConditions.map {
        when (it.type) {
            "TimeBasedCondition" -> TimeBasedCondition(it.value)
            "FixedRateCondition" -> FixedRateCondition(it.value)
            else -> throw TypeNotMatchException("${it.type} not match in BillingCondition")
        }
    }
    return ContractFactory.ContractUpdateCommand(
        period = ContractPeriod(
            contractStartDate,
            contractEndDate,
        ),
        status = ContractStatus.valueOf(this.status),
        billingConditions = billingConditions,
        note = this.note,
    )
}

class TypeNotMatchException(msg: String) : RuntimeException(msg)

private fun Contract.mapToDTO(): ContractApplicationService.ContractDTO = ContractApplicationService.ContractDTO(
    id = id.value.toString(),
    number = number.value,
    contractStartDate = period.startDate,
    contractEndDate = period.endDate,
    status = status.name,
    billingConditions = billingConditions.map {
        when (it) {
            is FixedRateCondition -> ContractApplicationService.BillingConditionDTO(
                type = "FixedRateCondition",
                value = it.fixedAmount
            )

            is TimeBasedCondition -> ContractApplicationService.BillingConditionDTO(
                type = "TimeBasedCondition",
                value = it.hourlyRate
            )
        }
    },
    note = note,
)
