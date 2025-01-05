package app.selfhr.domains.proprietor.vo

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class UUIDv7ProprietorIDGenerator : ProprietorIDGenerator {
    override fun generate(): ProprietorID = Generators
        .timeBasedGenerator()
        .generate()
        .let(::ProprietorID)
}
