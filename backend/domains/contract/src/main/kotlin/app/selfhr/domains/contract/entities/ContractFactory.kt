package app.selfhr.domains.contract.entities

import app.selfhr.domains.contract.vo.BillingCondition
import app.selfhr.domains.contract.vo.ContractIDGenerator
import app.selfhr.domains.contract.vo.ContractNo
import app.selfhr.domains.contract.vo.ContractPeriod
import app.selfhr.domains.contract.vo.ContractStatus
import app.selfhr.domains.contract.vo.ContractVersion
import app.selfhr.domains.proprietor.vo.ProprietorID
import com.github.michaelbull.result.getOrThrow
import org.springframework.stereotype.Component

@Component
class ContractFactory(
    private val idGenerator: ContractIDGenerator,
    private val updateValidator: ContractUpdateValidator,
    private val contractVersionBumpUpVerifier: ContractVersionBumpUpVerifier,
) {
    fun create(createCommand: ContractCreateCommand): Contract = Contract(
        id = idGenerator.generate(),
        proprietorId = createCommand.proprietorId,
        number = createCommand.number,
        period = createCommand.period,
        status = ContractStatus.DRAFT,
        billingConditions = createCommand.billingConditions,
        note = createCommand.note,
        version = ContractVersion.FIRST,
    )

    fun update(current: Contract, updateCommand: ContractUpdateCommand): Contract {
        updateValidator
            .validate(current, updateCommand)
            .getOrThrow()

        val (newId, newVersion) = if (contractVersionBumpUpVerifier.verify(current, updateCommand)) {
            idGenerator.generate() to current.version.bumpup()
        } else {
            current.id to current.version
        }

        return Contract(
            id = newId,
            proprietorId = current.proprietorId,
            number = current.number,
            period = updateCommand.period,
            status = updateCommand.status,
            billingConditions = updateCommand.billingConditions,
            note = updateCommand.note,
            version = newVersion,
        )
    }

    data class ContractCreateCommand(
        val proprietorId: ProprietorID,
        val number: ContractNo,
        val period: ContractPeriod,
        val billingConditions: List<BillingCondition>,
        val note: String?,
    )

    data class ContractUpdateCommand(
        val period: ContractPeriod,
        val status: ContractStatus,
        val billingConditions: List<BillingCondition>,
        val note: String?,
    )
}
