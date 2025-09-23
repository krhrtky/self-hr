package app.selfhr.domains.contract.rules

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.entities.ContractFactory
import app.selfhr.domains.contract.exceptions.ContractException
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Component

@Component
internal object PeriodUpdateRule : ContractUpdateRule {
    override fun verify(
        old: Contract,
        new: ContractFactory.ContractUpdateCommand
    ): Result<Unit, ContractException> = Ok(Unit)

    override fun needBumpUpVersion(
        old: Contract,
        new: ContractFactory.ContractUpdateCommand
    ): Boolean = old.period != new.period
}
