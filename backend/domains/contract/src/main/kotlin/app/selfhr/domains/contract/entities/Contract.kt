package app.selfhr.domains.contract.entities

import app.selfhr.domains.contract.exceptions.ContractException
import app.selfhr.domains.contract.vo.BillingCondition
import app.selfhr.domains.contract.vo.ContractID
import app.selfhr.domains.contract.vo.ContractNo
import app.selfhr.domains.contract.vo.ContractPeriod
import app.selfhr.domains.contract.vo.ContractStatus
import app.selfhr.domains.contract.vo.ContractVersion
import app.selfhr.domains.proprietor.vo.ProprietorID
import app.selfhr.shared.entity.Entity

@Suppress("LongParameterList")
class Contract internal constructor(
    override val id: ContractID,
    internal val proprietorId: ProprietorID,
    internal val number: ContractNo,
    internal val period: ContractPeriod,
    internal val status: ContractStatus,
    internal val billingConditions: List<BillingCondition>,
    internal val note: String?,
    internal val version: ContractVersion,
) : Entity<ContractID> {
    fun getBillingConditions(): List<BillingCondition> = billingConditions
}

class ContractValidationException(val errors: List<ContractException>) : RuntimeException()
