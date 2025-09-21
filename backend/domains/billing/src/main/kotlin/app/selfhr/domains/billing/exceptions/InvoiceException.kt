package app.selfhr.domains.billing.exceptions

sealed class InvoiceException(
    override val message: String?,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    class InvalidStatusTransitionException(
        message: String?,
        cause: Throwable? = null
    ) : InvoiceException(message, cause)
}
