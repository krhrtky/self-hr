package com.example.domains.entities.attendance.events

import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@JvmInline
value class AttendanceEventID(val value: UUID)

sealed interface AttendanceEvent {
    val id: AttendanceEventID
    val attendanceDate: LocalDate
    val timestamp: Timestamp

    class TimeRecordingEvent(
        override val id: AttendanceEventID,
        override val attendanceDate: LocalDate,
        override val timestamp: Timestamp,
        val recordAt: OffsetDateTime,
    ) : AttendanceEvent

    class TimeCorrectionEvent(
        override val id: AttendanceEventID,
        override val attendanceDate: LocalDate,
        override val timestamp: Timestamp,
        val correctAttendanceEventID: AttendanceEventID,
        val correctDateTime: OffsetDateTime,
    ) : AttendanceEvent
}
