package app.selfhr.domains.contract.rules

import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.entities.ContractFactory.ContractUpdateCommand
import app.selfhr.domains.contract.exceptions.ContractStatusInvalidStatusTransitionException
import app.selfhr.domains.contract.vo.ContractStatus
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.springframework.stereotype.Component

@Component
internal object StatusTransitionRule : ContractUpdateRule {
    override fun verify(
        old: Contract,
        new: ContractUpdateCommand
    ): Result<Unit, ContractStatusInvalidStatusTransitionException> =
        if (allowedTransitions(old.status).contains(new.status)) {
            Ok(Unit)
        } else {
            ContractStatusInvalidStatusTransitionException(
                "状態遷移が許可されていません: ${old.status} → ${new.status}"
            )
                .let(::Err)
        }

    override fun needBumpUpVersion(old: Contract, new: ContractUpdateCommand): Boolean = false

    private fun allowedTransitions(status: ContractStatus): Set<ContractStatus> = when (status) {
        ContractStatus.DRAFT -> setOf(ContractStatus.ACTIVE, ContractStatus.VOIDED)
        ContractStatus.ACTIVE -> setOf(ContractStatus.TERMINATED, ContractStatus.EXPIRED)
        ContractStatus.EXPIRED -> setOf(ContractStatus.RENEWED)
        ContractStatus.TERMINATED -> emptySet()
        ContractStatus.VOIDED -> emptySet()
        ContractStatus.RENEWED -> emptySet()
    }
}

