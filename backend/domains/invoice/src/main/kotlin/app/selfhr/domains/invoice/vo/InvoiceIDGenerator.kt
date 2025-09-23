package app.selfhr.domains.invoice.vo

import app.selfhr.shared.entity.IDGenerator

interface InvoiceIDGenerator : IDGenerator<InvoiceID> {
    companion object {
        fun create(): InvoiceIDGenerator = UUIDv7InvoiceIDGenerator()
    }
}
