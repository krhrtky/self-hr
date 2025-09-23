package app.selfhr.domains.invoice.vo

import app.selfhr.shared.entity.ID
import java.util.UUID

@JvmInline
value class InvoiceID internal constructor(override val value: UUID) : ID<UUID> {
    companion object {
        fun generate(): InvoiceID = InvoiceIDGenerator.create().generate()
    }
}
