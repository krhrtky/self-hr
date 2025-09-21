package app.selfhr.domains.contract.vo

sealed class BillingCondition

data class TimeBasedCondition(
    val hourlyRate: Double,
) : BillingCondition()

data class FixedRateCondition(
    val fixedAmount: Double,
) : BillingCondition()
