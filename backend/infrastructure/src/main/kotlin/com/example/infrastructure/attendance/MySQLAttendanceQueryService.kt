package com.example.infrastructure.attendance

import com.example.domains.entities.attendance.AttendanceEventDTO
import com.example.domains.entities.attendance.AttendanceQueryResult
import com.example.domains.entities.attendance.AttendanceQueryResultItem
import com.example.domains.entities.attendance.AttendanceQueryService
import com.example.domains.entities.attendance.AttendanceSearchParameters
import com.example.infrastructure.db.tables.records.AttendanceCorrectEventRecord
import com.example.infrastructure.db.tables.records.AttendanceRecordEventRecord
import com.example.infrastructure.db.tables.references.ATTENDANCE
import com.example.infrastructure.db.tables.references.ATTENDANCE_CORRECT_EVENT
import com.example.infrastructure.db.tables.references.ATTENDANCE_RECORD_EVENT
import org.jooq.DSLContext
import org.jooq.impl.DSL.max
import org.jooq.impl.DSL.select
import org.jooq.impl.DSL.table
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class MySQLAttendanceQueryService(
    private val context: DSLContext,
) : AttendanceQueryService {
    override fun query(params: AttendanceSearchParameters): AttendanceQueryResult {
        val attendanceEvents = fetchAttendanceEvent(params.userId, params.from, params.to)
        val attendanceGroupByAttendanceDate = attendanceEvents.groupBy { it.attendanceDate }

        val correctEventsGroupByAttendanceEventId =
            fetchAttendanceCorrectEvent(
                attendanceGroupByAttendanceDate.values.flatten().map { it.attendanceRecordEventId }
            )
                .groupBy { it.attendanceRecordEventId }

        val items = attendanceGroupByAttendanceDate
            .map {
                val first = it.value.minBy(AttendanceRecordEventRecord::recordDatetime)
                val start = correctEventsGroupByAttendanceEventId[first.attendanceRecordEventId]
                    ?.firstOrNull()
                    ?.mapToAttendanceEventDTO()
                    ?: first.mapToAttendanceEventDTO()

                val last = it.value.maxBy(AttendanceRecordEventRecord::recordDatetime)
                val end = correctEventsGroupByAttendanceEventId[last.attendanceRecordEventId]
                    ?.firstOrNull()
                    ?.mapToAttendanceEventDTO()
                    ?: last.mapToAttendanceEventDTO()

                AttendanceQueryResultItem(
                    attendanceDate = it.key,
                    startAt = start,
                    endAt = end.takeUnless { end == start },
                )
            }

        return AttendanceQueryResult(
            data = items,
        )
    }

    private fun fetchAttendanceEvent(userId: UUID, from: LocalDate, to: LocalDate) = context.select(
        ATTENDANCE.fields().toList() +
            ATTENDANCE_RECORD_EVENT.fields().toList(),
    )
        .from(ATTENDANCE)
        .join(ATTENDANCE_RECORD_EVENT).using(ATTENDANCE.ATTENDANCE_ID)
        .where(
            listOf(
                ATTENDANCE.USER_ID.eq(userId.toString()),
                ATTENDANCE.ATTENDANCE_DATE.between(from).and(to),
            )
        )
        .orderBy(ATTENDANCE.ATTENDANCE_DATE.asc())
        .fetchInto(ATTENDANCE_RECORD_EVENT)

    private fun fetchAttendanceCorrectEvent(attendanceEventIds: List<String>) = context.select(
        ATTENDANCE_CORRECT_EVENT.ATTENDANCE_RECORD_EVENT_ID,
        ATTENDANCE_CORRECT_EVENT.CORRECT_DATETIME,
        ATTENDANCE_CORRECT_EVENT.EVENT_CREATED_DATETIME,
    )
        .from(ATTENDANCE_CORRECT_EVENT)
        .innerJoin(
            table(
                select(
                    ATTENDANCE_CORRECT_EVENT.ATTENDANCE_RECORD_EVENT_ID,
                    max(ATTENDANCE_CORRECT_EVENT.EVENT_CREATED_DATETIME),
                )
                    .from(ATTENDANCE_CORRECT_EVENT)
                    .where(
                        ATTENDANCE_CORRECT_EVENT.ATTENDANCE_RECORD_EVENT_ID.`in`(attendanceEventIds)
                    )
                    .groupBy(ATTENDANCE_CORRECT_EVENT.ATTENDANCE_RECORD_EVENT_ID)

            ).`as`("sub")
        ).using(ATTENDANCE_CORRECT_EVENT.ATTENDANCE_RECORD_EVENT_ID)
        .fetchInto(ATTENDANCE_CORRECT_EVENT)

    private fun AttendanceRecordEventRecord.mapToAttendanceEventDTO() =
        AttendanceEventDTO(
            id = attendanceRecordEventId,
            time = recordDatetime,
        )
    private fun AttendanceCorrectEventRecord.mapToAttendanceEventDTO() =
        AttendanceEventDTO(
            id = attendanceRecordEventId,
            time = correctDatetime,
        )
}
