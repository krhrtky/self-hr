package app.selfhr.domains.billing.vo

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
