package app.selfhr.domains.contract.exceptions

abstract class ContractException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause)
