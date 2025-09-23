package app.selfhr.domains.proprietor.vo

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class UUIDv7ProprietorIDGenerator : ProprietorIDGenerator {
    override fun generate(): ProprietorID = Generators
        .timeBasedEpochGenerator()
        .generate()
        .let(::ProprietorID)

    override fun from(value: String): ProprietorID = value
        .takeIf(UUIDV7Pattern::matches)
        ?.let(UUID::fromString)
        ?.let(::ProprietorID)
        ?: throw UUIDNotMatchingPatternException("$value is not a valid UUID v7.")

    companion object {
        private val UUIDV7Pattern = Regex("^[^-]+-[^-]+-7[^-]+-[^-]+-[^-]+$")
    }
}

class UUIDNotMatchingPatternException(override val message: String) : Exception(message)
