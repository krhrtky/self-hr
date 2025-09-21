package app.selfhr.domains.contract.invoice.vo

import app.selfhr.shared.entity.IDGenerator

interface InvoiceIDGenerator : IDGenerator<InvoiceID> {
    companion object {
        fun create(): InvoiceIDGenerator = UUIDv7InvoiceIDGenerator()
    }
}
