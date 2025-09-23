package app.selfhr.domains.billing.vo

import app.selfhr.shared.entity.IDGenerator

interface InvoiceIDGenerator : IDGenerator<InvoiceID> {
    companion object {
        fun create(): InvoiceIDGenerator = UUIDv7InvoiceIDGenerator()
    }
}
