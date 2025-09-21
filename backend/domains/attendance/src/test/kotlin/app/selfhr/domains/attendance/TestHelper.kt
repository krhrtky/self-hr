package app.selfhr.domains.attendance

import app.selfhr.domains.contract.vo.ContractID
import app.selfhr.domains.contract.vo.ContractIDGenerator
import app.selfhr.domains.contract.vo.UUIDv7ContractIDGenerator

object TestHelper {
    private val contractIdGenerator: ContractIDGenerator = UUIDv7ContractIDGenerator()

    fun createContractId(): ContractID = contractIdGenerator.generate()
}
