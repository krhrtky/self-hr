package com.example.infrastructure.attendance

import com.example.infrastructure.db.tables.records.AttendanceCorrectEventRecord
import com.example.infrastructure.db.tables.records.AttendanceRecord
import com.example.infrastructure.db.tables.records.AttendanceRecordEventRecord
import com.example.infrastructure.db.tables.references.ATTENDANCE
import com.example.infrastructure.db.tables.references.ATTENDANCE_CORRECT_EVENT
import com.example.infrastructure.db.tables.references.ATTENDANCE_RECORD_EVENT
import org.jooq.DSLContext
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class AttendanceMutationQuery(
    private val context: DSLContext,
) {
    fun upsertAttendance(attendance: AttendanceRecord) = context
        .insertInto(ATTENDANCE)
        .set(attendance)
        .onDuplicateKeyUpdate()
        .set(ATTENDANCE.UPDATED_AT, OffsetDateTime.now())

    internal fun upsertEvent(event: AttendanceRecordEventRecord) = context
        .insertInto(ATTENDANCE_RECORD_EVENT)
        .set(event)
        .onDuplicateKeyUpdate()
        .set(ATTENDANCE_RECORD_EVENT.UPDATED_AT, OffsetDateTime.now())
    fun upsertAttendanceRecordEvents(events: List<AttendanceRecordEventRecord>) =
        events
            .map(::upsertEvent)
            .let(context::batch)

    internal fun upsertEvent(event: AttendanceCorrectEventRecord) = context
        .insertInto(ATTENDANCE_CORRECT_EVENT)
        .set(event)
        .onDuplicateKeyUpdate()
        .set(ATTENDANCE_CORRECT_EVENT.UPDATED_AT, OffsetDateTime.now())

    fun upsertAttendanceCorrectEvents(events: List<AttendanceCorrectEventRecord>) = events
        .map(::upsertEvent)
        .let(context::batch)
}
