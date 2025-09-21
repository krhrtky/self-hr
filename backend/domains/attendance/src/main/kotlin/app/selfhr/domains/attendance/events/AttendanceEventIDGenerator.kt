package app.selfhr.domains.attendance.events

import app.selfhr.shared.entity.IDGenerator

interface AttendanceEventIDGenerator : IDGenerator<AttendanceEventID> {
    companion object {
        fun create(): AttendanceEventIDGenerator = UUIDv7AttendanceEventIDGenerator()
    }
}
