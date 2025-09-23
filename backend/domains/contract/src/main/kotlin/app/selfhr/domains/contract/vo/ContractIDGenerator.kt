package app.selfhr.domains.contract.vo

import app.selfhr.shared.entity.IDGenerator

interface ContractIDGenerator : IDGenerator<ContractID> {
    override fun generate(): ContractID
}
