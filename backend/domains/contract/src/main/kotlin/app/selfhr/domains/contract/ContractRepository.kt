package app.selfhr.domains.contract

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.vo.ContractNo

interface ContractRepository {
    fun save(contract: Contract)
    fun find(contractNumber: ContractNo): Contract?
}
