package com.example.infrastructure.attendance

import com.example.domains.entities.attendance.AttendanceID
import com.example.domains.entities.users.UserID
import com.example.infrastructure.db.tables.records.AttendanceCorrectEventRecord
import com.example.infrastructure.db.tables.records.AttendanceRecord
import com.example.infrastructure.db.tables.records.AttendanceRecordEventRecord
import com.example.infrastructure.db.tables.references.ATTENDANCE
import com.example.infrastructure.db.tables.references.ATTENDANCE_CORRECT_EVENT
import com.example.infrastructure.db.tables.references.ATTENDANCE_RECORD_EVENT
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class AttendanceQuery(
    private val context: DSLContext,
) {
    fun findAttendanceBy(id: AttendanceID) = context
        .select(ATTENDANCE.fields().toList())
        .from(ATTENDANCE)
        .where(
            ATTENDANCE.ATTENDANCE_ID.eq(id.value.toString())
        )
        .query

    fun findEventsBy(id: AttendanceID) = context
        .select(
            ATTENDANCE_RECORD_EVENT.fields().toList() +
                ATTENDANCE_CORRECT_EVENT.fields().toList()
        )
        .from(ATTENDANCE_RECORD_EVENT)
        .leftJoin(ATTENDANCE_CORRECT_EVENT).using(ATTENDANCE_RECORD_EVENT.ATTENDANCE_RECORD_EVENT_ID)
        .where(ATTENDANCE_RECORD_EVENT.ATTENDANCE_RECORD_EVENT_ID.eq(id.value.toString()))
        .query

    fun findAttendanceBy(userID: UserID, attendanceDate: LocalDate) = context
        .select(ATTENDANCE.fields().toList())
        .from(ATTENDANCE)
        .where(
            listOf(
                ATTENDANCE.USER_ID.eq(userID.value),
                ATTENDANCE.ATTENDANCE_DATE.eq(attendanceDate),
            )
        )
        .query

    fun findAttendancesBy(userID: UserID) = context
        .select(ATTENDANCE.fields().toList())
        .from(ATTENDANCE)
        .where(
            ATTENDANCE.USER_ID.eq(userID.value),
        )
        .query

    fun findEventsBy(attendanceIDs: List<AttendanceID>) = context
        .select(
            ATTENDANCE_RECORD_EVENT.fields().toList() +
                ATTENDANCE_CORRECT_EVENT.fields().toList()
        )
        .from(ATTENDANCE_RECORD_EVENT)
        .leftJoin(ATTENDANCE_CORRECT_EVENT).using(ATTENDANCE_RECORD_EVENT.ATTENDANCE_RECORD_EVENT_ID)
        .where(ATTENDANCE_RECORD_EVENT.ATTENDANCE_ID.`in`(attendanceIDs.map { it.value }))
        .query

    fun upsertAttendance(attendance: AttendanceRecord) = context
        .insertInto(ATTENDANCE)
        .set(attendance)
        .onDuplicateKeyUpdate()
        .set(ATTENDANCE.UPDATED_AT, LocalDateTime.now())

    internal fun upsertEvent(event: AttendanceRecordEventRecord) = context
        .insertInto(ATTENDANCE_RECORD_EVENT)
        .set(event)
        .onDuplicateKeyUpdate()
        .set(ATTENDANCE_RECORD_EVENT.UPDATED_AT, LocalDateTime.now())
    fun upsertAttendanceRecordEvents(events: List<AttendanceRecordEventRecord>) =
        events
            .map(::upsertEvent)
            .let(context::batch)

    internal fun upsertEvent(event: AttendanceCorrectEventRecord) = context
        .insertInto(ATTENDANCE_CORRECT_EVENT)
        .set(event)
        .onDuplicateKeyUpdate()
        .set(ATTENDANCE_CORRECT_EVENT.UPDATED_AT, LocalDateTime.now())

    fun upsertAttendanceCorrectEvents(events: List<AttendanceCorrectEventRecord>) = events
        .map(::upsertEvent)
        .let(context::batch)
}
