package app.selfhr.domains.proprietor.vo

import app.selfhr.shared.entity.ID
import java.util.UUID

@JvmInline
value class ProprietorID internal constructor(override val value: UUID) : ID<UUID>
