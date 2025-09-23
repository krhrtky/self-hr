package app.selfhr.domains.contract.vo

import java.time.LocalDate

data class ContractPeriod(
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    init {
        require(startDate.isBefore(endDate)) {
            "startDate must be before endDate. startDate: $startDate, endDate: $endDate"
        }
    }
}
