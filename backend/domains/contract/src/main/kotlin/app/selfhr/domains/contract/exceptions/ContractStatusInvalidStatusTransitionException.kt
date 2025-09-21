package app.selfhr.domains.contract.exceptions

class ContractStatusInvalidStatusTransitionException(
    override val message: String,
    override val cause: Throwable? = null,
) : ContractException(message)
