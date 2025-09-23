package app.selfhr.domains.attendance.events

import app.selfhr.shared.entity.ID
import java.util.UUID

@JvmInline
value class AttendanceEventID internal constructor(override val value: UUID) : ID<UUID> {
    companion object {
        fun generate(): AttendanceEventID = AttendanceEventIDGenerator.create().generate()
    }
}
