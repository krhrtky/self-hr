package app.selfhr.domains.contract.entities

import app.selfhr.domains.contract.entities.ContractFactory.ContractUpdateCommand
import app.selfhr.domains.contract.rules.PeriodUpdateRule
import app.selfhr.domains.contract.rules.StatusTransitionRule
import org.springframework.stereotype.Component

@Component
object ContractVersionBumpUpVerifier {
    private val validateRules = setOf(
        StatusTransitionRule,
        PeriodUpdateRule,
    )

    fun verify(current: Contract, updateCommand: ContractUpdateCommand): Boolean = validateRules
        .map { it.needBumpUpVersion(current, updateCommand) }
        .let(List<Boolean>::any)
}
