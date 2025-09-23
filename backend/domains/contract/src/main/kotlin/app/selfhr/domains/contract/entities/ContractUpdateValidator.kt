package app.selfhr.domains.contract.entities

import app.selfhr.domains.contract.rules.PeriodUpdateRule
import app.selfhr.domains.contract.rules.StatusTransitionRule
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getError
import org.springframework.stereotype.Component

@Component
object ContractUpdateValidator {
    private val validateRules = setOf(
        StatusTransitionRule,
        PeriodUpdateRule,
    )

    fun validate(
        current: Contract,
        updateCommand: ContractFactory.ContractUpdateCommand
    ): Result<Unit, ContractValidationException> {
        val validationErrors = validateRules
            .map { it.verify(current, updateCommand) }
            .mapNotNull { it.getError() }

        return if (validationErrors.isNotEmpty()) {
            ContractValidationException(validationErrors)
                .let(::Err)
        } else {
            Ok(Unit)
        }
    }
}
