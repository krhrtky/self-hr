package app.selfhr.domains.invoice.vo

sealed class InvoiceItem {
    abstract val amount: Double

    data class TimeBasedItem(
        val hours: Double,
        val hourlyRate: Double,
    ) : InvoiceItem() {
        override val amount: Double by lazy { hourlyRate * hours }
    }

    data class FixedRateItem(
        override val amount: Double
    ) : InvoiceItem()
}
