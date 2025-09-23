package app.selfhr.domains.contract.vo

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class UUIDv7ContractIDGenerator : ContractIDGenerator {
    override fun generate(): ContractID = Generators
        .timeBasedGenerator()
        .generate()
        .let(::ContractID)
}
