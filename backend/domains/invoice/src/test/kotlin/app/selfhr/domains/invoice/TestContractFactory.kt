package app.selfhr.domains.invoice

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.entities.ContractFactory
import app.selfhr.domains.contract.entities.ContractUpdateValidator
import app.selfhr.domains.contract.entities.ContractVersionBumpUpVerifier
import app.selfhr.domains.contract.vo.BillingCondition
import app.selfhr.domains.contract.vo.ContractID
import app.selfhr.domains.contract.vo.ContractIDGenerator
import app.selfhr.domains.contract.vo.ContractNo
import app.selfhr.domains.contract.vo.ContractPeriod
import app.selfhr.domains.contract.vo.UUIDv7ContractIDGenerator
import app.selfhr.domains.proprietor.vo.ProprietorID
import java.util.UUID

class TestContractIDGenerator : ContractIDGenerator {
    private val idGenerator = UUIDv7ContractIDGenerator()
    override fun generate(): ContractID = idGenerator.generate()
}

object TestContractFactory {
    private val factory = ContractFactory(
        idGenerator = TestContractIDGenerator(),
        updateValidator = ContractUpdateValidator,
        contractVersionBumpUpVerifier = ContractVersionBumpUpVerifier
    )

    fun create(
        proprietorId: ProprietorID,
        period: ContractPeriod,
        billingConditions: List<BillingCondition>,
        note: String? = null
    ): Contract {
        val command = ContractFactory.ContractCreateCommand(
            proprietorId = proprietorId,
            number = ContractNo("TEST-${UUID.randomUUID()}"),
            period = period,
            billingConditions = billingConditions,
            note = note
        )
        return factory.create(command)
    }
}
