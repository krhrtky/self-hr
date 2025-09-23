package app.selfhr.domains.proprietor.entities

import app.selfhr.domains.proprietor.vo.EmailAddress
import app.selfhr.domains.proprietor.vo.ProprietorIDGenerator
import org.springframework.stereotype.Component

@Component
class ProprietorFactory(
    private val proprietorIDGenerator: ProprietorIDGenerator
) {
    fun create(email: EmailAddress): Proprietor {
        val id = proprietorIDGenerator.generate()
        return Proprietor(id, email)
    }
}
