package app.selfrh.domains.project.vo

import app.selfhr.shared.entity.ID
import java.util.UUID

@JvmInline
value class ProjectID internal constructor(override val value: UUID) : ID<UUID>
