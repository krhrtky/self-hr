package app.selfhr.domains.attendance.events

import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime

sealed interface AttendanceEvent {
    val id: AttendanceEventID
    val attendanceDate: LocalDate
    val timestamp: Timestamp

    data class TimeRecordingEvent(
        override val id: AttendanceEventID,
        override val attendanceDate: LocalDate,
        override val timestamp: Timestamp,
        val recordAt: OffsetDateTime,
    ) : AttendanceEvent

    data class TimeCorrectionEvent(
        override val id: AttendanceEventID,
        override val attendanceDate: LocalDate,
        override val timestamp: Timestamp,
        val correctAttendanceEventID: AttendanceEventID,
        val correctDateTime: OffsetDateTime,
    ) : AttendanceEvent
}
