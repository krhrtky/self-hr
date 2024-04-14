package com.example.infrastructure.attendance

import com.example.domains.entities.attendance.AttendanceID
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.users.UserID
import com.example.infrastructure.db.tables.references.ATTENDANCE
import com.example.infrastructure.db.tables.references.ATTENDANCE_CORRECT_EVENT
import com.example.infrastructure.db.tables.references.ATTENDANCE_RECORD_EVENT
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class AttendanceSelectQuery(
    private val context: DSLContext,
) {
    fun findAttendanceBy(id: AttendanceID) = context
        .select(ATTENDANCE.fields().toList())
        .from(ATTENDANCE)
        .where(
            ATTENDANCE.ATTENDANCE_ID.eq(id.value.toString())
        )
        .query

    fun findAttendanceBy(id: AttendanceEventID) = context
        .select(ATTENDANCE.fields().toList())
        .from(ATTENDANCE)
        .join(ATTENDANCE_RECORD_EVENT).using(ATTENDANCE.ATTENDANCE_ID)
        .where(
            ATTENDANCE_RECORD_EVENT.ATTENDANCE_RECORD_EVENT_ID.eq(id.value.toString())
        )
        .query

    fun findRecordEventsBy(id: AttendanceID) = context
        .select(
            ATTENDANCE_RECORD_EVENT.fields().toList()
        )
        .from(ATTENDANCE_RECORD_EVENT)
        .where(ATTENDANCE_RECORD_EVENT.ATTENDANCE_ID.eq(id.value.toString()))
        .query

    fun findCorrectEventsBy(id: AttendanceID) = context
        .select(
            ATTENDANCE_CORRECT_EVENT.fields().toList()
        )
        .from(ATTENDANCE_CORRECT_EVENT)
        .where(ATTENDANCE_CORRECT_EVENT.ATTENDANCE_ID.eq(id.value.toString()))
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

    fun findEventsBy(attendanceIDs: List<AttendanceID>) = context
        .select(
            ATTENDANCE_RECORD_EVENT.fields().toList() +
                ATTENDANCE_CORRECT_EVENT.fields().toList()
        )
        .from(ATTENDANCE_RECORD_EVENT)
        .leftJoin(ATTENDANCE_CORRECT_EVENT).using(ATTENDANCE_RECORD_EVENT.ATTENDANCE_RECORD_EVENT_ID)
        .where(ATTENDANCE_RECORD_EVENT.ATTENDANCE_ID.`in`(attendanceIDs.map { it.value }))
        .query
}
