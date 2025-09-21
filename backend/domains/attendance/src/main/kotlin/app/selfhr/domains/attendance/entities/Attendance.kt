package app.selfhr.domains.attendance.entities

import app.selfhr.domains.attendance.events.AttendanceEvent
import app.selfhr.domains.attendance.events.AttendanceEventID
import app.selfhr.domains.attendance.events.AttendanceEventIDGenerator
import app.selfhr.domains.attendance.exceptions.AttendanceException
import app.selfhr.domains.attendance.vo.AttendanceID
import app.selfhr.domains.contract.vo.ContractID
import app.selfhr.shared.attendance.AttendanceCalculator
import app.selfhr.shared.entity.Entity
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDate
import java.time.OffsetDateTime

class Attendance internal constructor(
    override val id: AttendanceID,
    internal val contractId: ContractID,
    internal val attendanceDate: LocalDate,
    internal val attendanceRecords: List<AttendanceEvent>
) : Entity<AttendanceID>, AttendanceCalculator {

    override fun calculateTotalHours(): Double {
        val timeRecords = attendanceRecords.filterIsInstance<AttendanceEvent.TimeRecordingEvent>()
        val corrections = attendanceRecords.filterIsInstance<AttendanceEvent.TimeCorrectionEvent>()

        // Apply corrections
        val effectiveRecords = timeRecords.map { record ->
            val correction = corrections.find { it.correctAttendanceEventID == record.id }
            correction?.correctDateTime ?: record.recordAt
        }

        // Calculate total duration if we have even number of records (in/out pairs)
        return if (effectiveRecords.size % 2 == 0) {
            effectiveRecords.chunked(2).sumOf { (start, end) ->
                Duration.between(start, end).toMinutes().toDouble() / MINUTES_PER_HOUR
            }
        } else {
            0.0 // Incomplete records
        }
    }

    fun record(recordTime: OffsetDateTime): Attendance {
        val eventId = AttendanceEventIDGenerator.create().generate()
        val now = OffsetDateTime.now()

        val event = AttendanceEvent.TimeRecordingEvent(
            id = eventId,
            attendanceDate = attendanceDate,
            timestamp = Timestamp.from(now.toInstant()),
            recordAt = recordTime
        )

        return Attendance(
            id = id,
            contractId = contractId,
            attendanceDate = attendanceDate,
            attendanceRecords = attendanceRecords + event
        )
    }

    fun correct(
        correctTarget: AttendanceEventID,
        correctDateTime: OffsetDateTime
    ): Attendance {
        val targetEvent = attendanceRecords.find { it.id == correctTarget }
            ?: throw AttendanceException.CorrectTargetDoesNotExistsException(
                "correctTarget(AttendanceEventID: ${correctTarget.value}) does not exist."
            )

        val eventId = AttendanceEventIDGenerator.create().generate()
        val now = OffsetDateTime.now()

        val event = AttendanceEvent.TimeCorrectionEvent(
            id = eventId,
            attendanceDate = attendanceDate,
            timestamp = Timestamp.from(now.toInstant()),
            correctAttendanceEventID = targetEvent.id,
            correctDateTime = correctDateTime
        )

        return Attendance(
            id = id,
            contractId = contractId,
            attendanceDate = attendanceDate,
            attendanceRecords = attendanceRecords + event
        )
    }

    companion object {
        private const val MINUTES_PER_HOUR = 60.0

        fun create(
            contractId: ContractID,
            attendanceDate: LocalDate,
        ) = Attendance(
            id = AttendanceID.generate(),
            contractId = contractId,
            attendanceDate = attendanceDate,
            attendanceRecords = emptyList()
        )
    }
}
