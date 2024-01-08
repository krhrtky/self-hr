package com.example.domains.entities.attendance

import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.users.UserID
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@JvmInline
value class AttendanceID(val value: UUID)
class Attendance private constructor(
    val id: AttendanceID,
    internal val userId: UserID,
    internal val attendanceDate: LocalDate,
    internal val attendanceRecords: List<AttendanceEvent>,
) {
    fun record(recordTime: OffsetDateTime): Result<Pair<Attendance, AttendanceEvent>, AttendanceException> {
        val now = OffsetDateTime.now()

        return AttendanceEvent
            .TimeRecordingEvent(
                id = AttendanceEventID(UUID.randomUUID()),
                recordAt = recordTime,
                attendanceDate = attendanceDate,
                timestamp = now.toInstant().let(Timestamp::from),
            )
            .let {
                Attendance(
                    id = id,
                    userId = userId,
                    attendanceDate = attendanceDate,
                    attendanceRecords = attendanceRecords + it
                ) to it
            }
            .let(::Ok)
    }

    fun correct(
        correctTarget: AttendanceEventID,
        correctDateTime: OffsetDateTime,
    ): Result<Pair<Attendance, AttendanceEvent>, AttendanceException> {
        val now = OffsetDateTime.now()
        val event = attendanceRecords
            .find { it.id == correctTarget }
            ?.let {
                AttendanceEvent.TimeCorrectionEvent(
                    id = AttendanceEventID(UUID.randomUUID()),
                    correctDateTime = correctDateTime,
                    timestamp = now.toInstant().let(Timestamp::from),
                    correctAttendanceEventID = it.id,
                    attendanceDate = attendanceDate,
                )
            }
            ?: return AttendanceException
                .CorrectTargetDoesNotExistsException(
                    "correctTarget(AttendanceEventID: ${correctTarget.value}) is not exists."
                )
                .let(::Err)

        return Pair(
            Attendance(
                id = id,
                userId = userId,
                attendanceDate = attendanceDate,
                attendanceRecords = attendanceRecords + event
            ),
            event,
        )
            .let(::Ok)
    }

    companion object {
        fun create(
            userId: UserID,
            attendanceDate: LocalDate,
        ) = Attendance(
            id = UUID.randomUUID().let(::AttendanceID),
            userId = userId,
            attendanceDate = attendanceDate,
            attendanceRecords = emptyList(),
        )

        internal fun fromRepository(
            id: AttendanceID,
            userId: UserID,
            attendanceDate: LocalDate,
            attendanceRecords: List<AttendanceEvent>,
        ) = Attendance(
            id = id,
            userId = userId,
            attendanceDate = attendanceDate,
            attendanceRecords = attendanceRecords,
        )
    }
}

sealed class AttendanceException(
    override val message: String?,
    override val cause: Throwable?
) : Exception(message, cause) {
    class AttendanceNotExistsException(message: String?, cause: Throwable? = null) : AttendanceException(
        message,
        cause
    )
    class CorrectTargetDoesNotExistsException(message: String?, cause: Throwable? = null) : AttendanceException(
        message,
        cause
    )
}
