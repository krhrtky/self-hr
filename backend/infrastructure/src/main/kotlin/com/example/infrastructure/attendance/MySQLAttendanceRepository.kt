package com.example.infrastructure.attendance

import com.example.domains.entities.attendance.AttendanceID
import com.example.domains.entities.attendance.AttendanceRepository
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.users.UserID
import com.example.infrastructure.db.tables.records.AttendanceCorrectEventRecord
import com.example.infrastructure.db.tables.records.AttendanceRecord
import com.example.infrastructure.db.tables.records.AttendanceRecordEventRecord
import com.example.infrastructure.db.tables.references.ATTENDANCE
import com.example.infrastructure.db.tables.references.ATTENDANCE_CORRECT_EVENT
import com.example.infrastructure.db.tables.references.ATTENDANCE_RECORD_EVENT
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.*

@Repository
class MySQLAttendanceRepository(
    private val query: AttendanceSelectQuery,
    private val mutation: AttendanceMutationQuery,
) : AttendanceRepository() {
    override fun findImpl(id: AttendanceID): AttendanceRaw? {
        val attendanceRecord = query
            .findAttendanceBy(id)
            .fetchOneInto(ATTENDANCE)
            ?: return null

        val recordEventsRecord = attendanceRecord.attendanceId
            .let(UUID::fromString)
            .let(::AttendanceID)
            .let(query::findRecordEventsBy)
            .fetch()

        val correctEventsRecord = attendanceRecord.attendanceId
            .let(UUID::fromString)
            .let(::AttendanceID)
            .let(query::findCorrectEventsBy)
            .fetch()

        return convertToAttendanceRaw(
            attendanceRecord,
            recordEventsRecord.into(ATTENDANCE_RECORD_EVENT),
            correctEventsRecord.into(ATTENDANCE_CORRECT_EVENT),
        )
    }

    override fun findImpl(id: AttendanceEventID): AttendanceRaw? {
        val attendanceRecord = query
            .findAttendanceBy(id)
            .fetchOneInto(ATTENDANCE)
            ?: return null

        val recordEventsRecord = attendanceRecord.attendanceId
            .let(UUID::fromString)
            .let(::AttendanceID)
            .let(query::findRecordEventsBy)
            .fetch()

        val correctEventsRecord = attendanceRecord.attendanceId
            .let(UUID::fromString)
            .let(::AttendanceID)
            .let(query::findCorrectEventsBy)
            .fetch()

        return convertToAttendanceRaw(
            attendanceRecord,
            recordEventsRecord.into(ATTENDANCE_RECORD_EVENT),
            correctEventsRecord.into(ATTENDANCE_CORRECT_EVENT),
        )
    }

    override fun findImpl(userID: UserID, attendanceDate: LocalDate): AttendanceRaw? {
        val attendanceRecord = query
            .findAttendanceBy(userID, attendanceDate)
            .fetchOneInto(ATTENDANCE)
            ?: return null

        val recordEventsRecord = attendanceRecord.attendanceId
            .let(UUID::fromString)
            .let(::AttendanceID)
            .let(query::findRecordEventsBy)
            .fetch()

        val correctEventsRecord = attendanceRecord.attendanceId
            .let(UUID::fromString)
            .let(::AttendanceID)
            .let(query::findCorrectEventsBy)
            .fetch()

        return convertToAttendanceRaw(
            attendanceRecord,
            recordEventsRecord.into(ATTENDANCE_RECORD_EVENT),
            correctEventsRecord.into(ATTENDANCE_CORRECT_EVENT),
        )
    }

    override fun save(attendanceRaw: AttendanceRaw) {
        attendanceRaw.let {
            AttendanceRecord(
                attendanceId = it.id.toString(),
                userId = it.userId.toString(),
                attendanceDate = it.attendanceDate,
            )
        }
            .let(mutation::upsertAttendance)
            .execute()

        attendanceRaw.events
            .filterIsInstance<EventRaw.TimeRecordingEventRaw>()
            .map {
                AttendanceRecordEventRecord(
                    attendanceRecordEventId = it.id.toString(),
                    attendanceId = attendanceRaw.id.toString(),
                    attendanceDate = attendanceRaw.attendanceDate,
                    recordDatetime = it.recordAt,
                    eventCreatedDatetime = it.timestamp.toInstant().atOffset(ZoneOffset.of("+09:00:00"))
                )
            }
            .let(mutation::upsertAttendanceRecordEvents)
            .execute()

        attendanceRaw.events
            .filterIsInstance<EventRaw.TimeCorrectionEventRaw>()
            .map {
                AttendanceCorrectEventRecord(
                    attendanceCorrectEventId = it.id.toString(),
                    attendanceId = attendanceRaw.id.toString(),
                    attendanceRecordEventId = it.correctAttendanceEventID.toString(),
                    attendanceDate = attendanceRaw.attendanceDate,
                    correctDatetime = it.correctDateTime,
                    eventCreatedDatetime = it.timestamp.toInstant().atOffset(ZoneOffset.of("+09:00:00"))
                )
            }
            .let(mutation::upsertAttendanceCorrectEvents)
            .execute()
    }

    private fun convertToAttendanceRaw(
        attendanceRecord: AttendanceRecord,
        attendanceRecordEventRecords: List<AttendanceRecordEventRecord>,
        attendanceCorrectEventRecords: List<AttendanceCorrectEventRecord>,
    ): AttendanceRaw =
        AttendanceRaw(
            id = UUID.fromString(attendanceRecord.attendanceId),
            userId = UUID.fromString(attendanceRecord.userId),
            attendanceDate = attendanceRecord.attendanceDate,
            events = (
                attendanceRecordEventRecords.map { it.toRaw() } +
                    attendanceCorrectEventRecords.map { it.toRaw() }
                ).sortedBy { it.timestamp }
        )

    private fun AttendanceRecordEventRecord.toRaw(): EventRaw = EventRaw.TimeRecordingEventRaw(
        id = UUID.fromString(attendanceRecordEventId),
        recordAt = this.recordDatetime,
        attendanceDate = attendanceDate,
        timestamp = eventCreatedDatetime.toInstant().let(Timestamp::from),
    )

    private fun AttendanceCorrectEventRecord.toRaw(): EventRaw = EventRaw.TimeCorrectionEventRaw(
        id = UUID.fromString(attendanceCorrectEventId),
        correctDateTime = correctDatetime,
        timestamp = eventCreatedDatetime.toInstant().let(Timestamp::from),
        correctAttendanceEventID = UUID.fromString(attendanceRecordEventId),
        attendanceDate = attendanceDate,
    )
}
