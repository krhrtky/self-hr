package app.selfrh.domains.project.vo

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class UUIDv7ProjectIDGenerator : ProjectIDGenerator {
    override fun generate(): ProjectID = Generators
        .timeBasedGenerator()
        .generate()
        .let(::ProjectID)
}
