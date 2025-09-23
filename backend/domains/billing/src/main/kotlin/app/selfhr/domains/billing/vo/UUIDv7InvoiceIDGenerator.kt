package app.selfhr.domains.billing.vo

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class UUIDv7InvoiceIDGenerator : InvoiceIDGenerator {
    override fun generate(): InvoiceID = Generators
        .timeBasedGenerator()
        .generate()
        .let(::InvoiceID)
}
