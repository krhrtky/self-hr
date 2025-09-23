package app.selfhr.domains.contract.rules

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.entities.ContractFactory.ContractUpdateCommand
import app.selfhr.domains.contract.exceptions.ContractException
import com.github.michaelbull.result.Result

internal interface ContractUpdateRule {
    fun verify(old: Contract, new: ContractUpdateCommand): Result<Unit, ContractException>
    fun needBumpUpVersion(old: Contract, new: ContractUpdateCommand): Boolean
}
