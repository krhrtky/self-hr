package app.selfhr.domains.contract

import app.selfhr.domains.contract.vo.ContractID
import app.selfhr.domains.contract.vo.ContractIDGenerator
import app.selfhr.domains.contract.vo.UUIDv7ContractIDGenerator

class TestContractIDGenerator : ContractIDGenerator {
    private val generator = UUIDv7ContractIDGenerator()
    override fun generate(): ContractID = generator.generate()
}
