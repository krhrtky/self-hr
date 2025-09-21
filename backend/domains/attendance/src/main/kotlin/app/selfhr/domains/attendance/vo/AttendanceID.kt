package app.selfhr.domains.attendance.vo

import app.selfhr.shared.entity.ID
import java.util.UUID

@JvmInline
value class AttendanceID internal constructor(override val value: UUID) : ID<UUID> {
    companion object {
        fun generate(): AttendanceID = AttendanceIDGenerator.create().generate()
    }
}
