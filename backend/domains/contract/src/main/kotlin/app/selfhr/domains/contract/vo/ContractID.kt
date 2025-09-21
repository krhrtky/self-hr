package app.selfhr.domains.contract.vo

import app.selfhr.shared.entity.ID
import java.util.UUID

@JvmInline
value class ContractID internal constructor(override val value: UUID) : ID<UUID>
