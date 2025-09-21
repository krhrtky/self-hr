package app.selfhr.domains.contract.invoice.vo

sealed class InvoiceItem {
    abstract val amount: Double

    data class TimeBasedItem(
        val hours: Double,
        val hourlyRate: Double,
        override val amount: Double
    ) : InvoiceItem()

    data class FixedRateItem(
        override val amount: Double
    ) : InvoiceItem()
}
