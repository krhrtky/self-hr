package app.selfhr.domains.invoice.vo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class InvoiceIDTest {
    @Test
    fun `generate should create unique IDs`() {
        val id1 = InvoiceID.generate()
        val id2 = InvoiceID.generate()

        assertThat(id1).isNotNull()
        assertThat(id2).isNotNull()
        assertThat(id1).isNotEqualTo(id2)
        assertThat(id1.value).isInstanceOf(UUID::class.java)
        assertThat(id2.value).isInstanceOf(UUID::class.java)
    }

    @Test
    fun `generated ID should be time-ordered`() {
        val id1 = InvoiceID.generate()
        Thread.sleep(10) // Ensure some time passes
        val id2 = InvoiceID.generate()

        // UUIDv7 is time-ordered, so comparing as strings should show the order
        assertThat(id1.value.toString())
            .isLessThan(id2.value.toString())
    }
}
