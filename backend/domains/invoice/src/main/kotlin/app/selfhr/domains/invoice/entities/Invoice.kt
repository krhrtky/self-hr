package app.selfhr.domains.invoice.entities

import app.selfhr.domains.attendance.entities.Attendance
import app.selfhr.domains.contract.entities.Contract
import app.selfhr.domains.contract.vo.BillingCondition
import app.selfhr.domains.contract.vo.ContractID
import app.selfhr.domains.contract.vo.FixedRateCondition
import app.selfhr.domains.contract.vo.TimeBasedCondition
import app.selfhr.domains.invoice.exceptions.InvoiceException
import app.selfhr.domains.invoice.vo.InvoiceID
import app.selfhr.domains.invoice.vo.InvoiceItem
import app.selfhr.domains.invoice.vo.InvoiceStatus
import app.selfhr.shared.entity.Entity
import java.time.YearMonth

class Invoice internal constructor(
    override val id: InvoiceID,
    internal val contractId: ContractID,
    internal val billingMonth: YearMonth,
    internal val items: List<InvoiceItem>,
    internal val status: InvoiceStatus
) : Entity<InvoiceID> {

    fun calculate(contract: Contract, attendances: List<Attendance>): Invoice {
        require(status == InvoiceStatus.DRAFT) { "Can only calculate draft invoices" }

        val items = contract.billingConditions.map { condition ->
            when (condition) {
                is TimeBasedCondition -> {
                    val totalHours = attendances.sumOf { it.calculateTotalHours() }
                    InvoiceItem.TimeBasedItem(
                        hours = totalHours,
                        hourlyRate = condition.hourlyRate,
                        amount = totalHours * condition.hourlyRate
                    )
                }
                is FixedRateCondition -> {
                    InvoiceItem.FixedRateItem(condition.fixedAmount)
                }
            }
        }

        return Invoice(id, contractId, billingMonth, items, status)
    }

    fun issue(): Invoice {
        require(status == InvoiceStatus.DRAFT) {
            throw InvoiceException.InvalidStatusTransitionException("Can only issue draft invoices")
        }
        return Invoice(id, contractId, billingMonth, items, InvoiceStatus.ISSUED)
    }

    fun pay(): Invoice {
        require(status == InvoiceStatus.ISSUED) {
            throw InvoiceException.InvalidStatusTransitionException("Can only pay issued invoices")
        }
        return Invoice(id, contractId, billingMonth, items, InvoiceStatus.PAID)
    }

    companion object {
        fun create(
            contractId: ContractID,
            billingMonth: YearMonth,
        ) = Invoice(
            id = InvoiceID.generate(),
            contractId = contractId,
            billingMonth = billingMonth,
            items = emptyList(),
            status = InvoiceStatus.DRAFT
        )
    }
}
