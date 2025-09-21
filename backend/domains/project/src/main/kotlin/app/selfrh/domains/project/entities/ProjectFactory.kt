package app.selfrh.domains.project.entities

import app.selfrh.domains.project.vo.ProjectIDGenerator
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class ProjectFactory(
    private val projectIDGenerator: ProjectIDGenerator
) {
    fun create(createCommand: ProjectCreateCommand): Project {
        TODO()
    }

    data class ProjectCreateCommand(
        val name: String,
        val description: String?,
        val startDate: OffsetDateTime,
        val endDate: OffsetDateTime,
    )
}
