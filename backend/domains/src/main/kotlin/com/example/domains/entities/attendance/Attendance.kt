package com.example.domains.entities.attendance

import com.example.domains.entities.attendance.AttendanceException.AttendanceNotExistsException
import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.users.UserID
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.map
import com.github.michaelbull.result.toResultOr
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@JvmInline
value class AttendanceID(val value: UUID)
class Attendance internal constructor(
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
            .toResultOr {
                AttendanceNotExistsException("")
            }
            .map {
                Attendance(
                    id = id,
                    userId = userId,
                    attendanceDate = attendanceDate,
                    attendanceRecords = attendanceRecords + it
                ) to it
            }
    }

    fun correct(
        correctTarget: AttendanceEventID,
        correctDateTime: OffsetDateTime,
    ): Result<Pair<Attendance, AttendanceEvent>, AttendanceException> {
        val now = OffsetDateTime.now()
        return attendanceRecords
            .find { it.id == correctTarget }
            .toResultOr {
                AttendanceException
                    .CorrectTargetDoesNotExistsException(
                        "correctTarget(AttendanceEventID: ${correctTarget.value}) is not exists."
                    )
            }
            .map {
                AttendanceEvent.TimeCorrectionEvent(
                    id = AttendanceEventID(UUID.randomUUID()),
                    correctDateTime = correctDateTime,
                    timestamp = now.toInstant().let(Timestamp::from),
                    correctAttendanceEventID = it.id,
                    attendanceDate = attendanceDate,
                )
            }
            .map {
                Pair(
                    Attendance(
                        id = id,
                        userId = userId,
                        attendanceDate = attendanceDate,
                        attendanceRecords = attendanceRecords + it
                    ),
                    it,
                )
            }
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
