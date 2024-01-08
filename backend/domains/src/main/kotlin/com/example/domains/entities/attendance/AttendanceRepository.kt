package com.example.domains.entities.attendance

import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.users.UserID
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

abstract class AttendanceRepository {
    fun find(id: AttendanceID): Attendance? =
        findImpl(id) ?.toEntity()
    fun find(userID: UserID, attendanceDate: LocalDate): Attendance? =
        findImpl(userID, attendanceDate)?.toEntity()
    fun find(userID: UserID): List<Attendance> =
        findImpl(userID).map { it.toEntity() }

    protected abstract fun findImpl(id: AttendanceID): AttendanceRaw?
    protected abstract fun findImpl(userID: UserID, attendanceDate: LocalDate): AttendanceRaw?
    protected abstract fun findImpl(userID: UserID): List<AttendanceRaw>

    fun save(attendance: Attendance) {
        AttendanceRaw(
            id = attendance.id.value,
            userId = attendance.userId.value.let(UUID::fromString),
            attendanceDate = attendance.attendanceDate,
            events = attendance.attendanceRecords.map { it.toRow() }
        ).let(::save)
    }

    protected abstract fun save(attendanceRaw: AttendanceRaw)
    protected data class AttendanceRaw(
        val id: UUID,
        val userId: UUID,
        val attendanceDate: LocalDate,
        val events: List<EventRaw>,
    ) {
        fun toEntity(): Attendance =
            Attendance.fromRepository(
                id = AttendanceID(id),
                userId = UserID(userId),
                attendanceDate = attendanceDate,
                attendanceRecords = events.map { it.toEntity() }
            )
    }

    protected sealed interface EventRaw {
        val id: UUID
        val attendanceDate: LocalDate
        val timestamp: Timestamp

        fun toEntity(): AttendanceEvent

        data class TimeRecordingEventRaw(
            override val id: UUID,
            override val attendanceDate: LocalDate,
            override val timestamp: Timestamp,
            val recordAt: OffsetDateTime,
        ) : EventRaw {
            override fun toEntity(): AttendanceEvent = AttendanceEvent.TimeRecordingEvent(
                id = AttendanceEventID(id),
                attendanceDate = attendanceDate,
                timestamp = timestamp,
                recordAt = recordAt,
            )
        }

        data class TimeCorrectionEventRaw(
            override val id: UUID,
            override val attendanceDate: LocalDate,
            override val timestamp: Timestamp,
            val correctAttendanceEventID: UUID,
            val correctDateTime: OffsetDateTime,
        ) : EventRaw {
            override fun toEntity(): AttendanceEvent = AttendanceEvent.TimeCorrectionEvent(
                id = AttendanceEventID(id),
                attendanceDate = attendanceDate,
                timestamp = timestamp,
                correctAttendanceEventID = AttendanceEventID(correctAttendanceEventID),
                correctDateTime = correctDateTime,
            )
        }
    }

    private fun AttendanceEvent.toRow() =
        when (this) {
            is AttendanceEvent.TimeRecordingEvent -> EventRaw.TimeRecordingEventRaw(
                id = id.value,
                attendanceDate,
                timestamp,
                recordAt
            )
            is AttendanceEvent.TimeCorrectionEvent -> EventRaw.TimeCorrectionEventRaw(
                id = id.value,
                attendanceDate,
                timestamp,
                correctAttendanceEventID = correctAttendanceEventID.value,
                correctDateTime,
            )
        }
}
