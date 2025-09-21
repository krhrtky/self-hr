package app.selfhr.domains.attendance.vo

import app.selfhr.shared.entity.IDGenerator

interface AttendanceIDGenerator : IDGenerator<AttendanceID> {
    companion object {
        fun create(): AttendanceIDGenerator = UUIDv7AttendanceIDGenerator()
    }
}
