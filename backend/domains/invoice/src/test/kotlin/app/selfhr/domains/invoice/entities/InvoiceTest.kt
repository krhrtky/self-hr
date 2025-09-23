package app.selfhr.domains.invoice.entities

import app.selfhr.domains.attendance.TestHelper
import app.selfhr.domains.contract.vo.BillingCondition
import app.selfhr.domains.invoice.vo.InvoiceID
import java.time.LocalDate
import java.time.YearMonth
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InvoiceTest {
    private val contractId = TestHelper.createContractId()
    private val billingMonth = YearMonth.now()

    @Test
    fun `create should initialize invoice with empty items`() {
        val invoice = Invoice.create(
            contractId = contractId,
            billingMonth = billingMonth
        )

        assertThat(invoice.id).isNotNull()
        assertThat(invoice.contractId).isEqualTo(contractId)
        assertThat(invoice.billingMonth).isEqualTo(billingMonth)
        assertThat(invoice.items).isEmpty()
        assertThat(invoice.status).isEqualTo(InvoiceStatus.DRAFT)
    }

    @Test
    fun `calculate should create items based on billing conditions`() {
        val timeBasedCondition = BillingCondition.TimeBasedCondition(hourlyRate = 5000.0)
        val fixedRateCondition = BillingCondition.FixedRateCondition(fixedAmount = 100000.0)
        val contract = Contract.create(
            proprietorId = ProprietorID.generate(),
            billingConditions = listOf(timeBasedCondition, fixedRateCondition)
        )

        val invoice = Invoice.create(contractId, billingMonth)
        val calculatedInvoice = invoice.calculate(contract, emptyList())

        assertThat(calculatedInvoice.items).hasSize(2)
        assertThat(calculatedInvoice.status).isEqualTo(InvoiceStatus.DRAFT)
    }

    @Test
    fun `issue should change status to ISSUED`() {
        val invoice = Invoice.create(contractId, billingMonth)
        val issuedInvoice = invoice.issue()

        assertThat(issuedInvoice.status).isEqualTo(InvoiceStatus.ISSUED)
    }

    @Test
    fun `pay should change status to PAID`() {
        val invoice = Invoice.create(contractId, billingMonth).issue()
        val paidInvoice = invoice.pay()

        assertThat(paidInvoice.status).isEqualTo(InvoiceStatus.PAID)
    }

    @Test
    fun `pay should throw exception when invoice is not issued`() {
        val invoice = Invoice.create(contractId, billingMonth)

        assertThrows<InvoiceException.InvalidStatusTransitionException> {
            invoice.pay()
        }
    }
}
