package app.selfhr.domains.contract.invoice.entities

import app.selfhr.domains.contract.TestContractFactory
import app.selfhr.domains.contract.invoice.exceptions.InvoiceException
import app.selfhr.domains.contract.invoice.vo.InvoiceStatus
import java.time.YearMonth
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test

class InvoiceTest {
    private val contract = TestContractFactory.createMixedContract()
    private val contractId = contract.id
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
