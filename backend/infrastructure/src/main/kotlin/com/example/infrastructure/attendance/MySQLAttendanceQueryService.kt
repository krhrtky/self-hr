package com.example.infrastructure.attendance

import com.example.domains.entities.attendance.AttendanceQueryResult
import com.example.domains.entities.attendance.AttendanceQueryResultItem
import com.example.domains.entities.attendance.AttendanceQueryService
import com.example.domains.entities.attendance.AttendanceSearchParameters
import com.example.infrastructure.db.tables.references.ATTENDANCE
import com.example.infrastructure.db.tables.references.ATTENDANCE_CORRECT_EVENT
import com.example.infrastructure.db.tables.references.ATTENDANCE_RECORD_EVENT
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class MySQLAttendanceQueryService(
    private val context: DSLContext,
) : AttendanceQueryService {
    override fun query(params: AttendanceSearchParameters): AttendanceQueryResult =
        context.select(
            ATTENDANCE.fields().toList() +
                ATTENDANCE_RECORD_EVENT.fields().toList(),
        )
            .from(ATTENDANCE)
            .join(ATTENDANCE_RECORD_EVENT).using(ATTENDANCE.ATTENDANCE_ID)
            .leftJoin(ATTENDANCE_CORRECT_EVENT).using(ATTENDANCE_RECORD_EVENT.ATTENDANCE_RECORD_EVENT_ID)
            .where(
                listOf(
                    ATTENDANCE.USER_ID.eq(params.userId.toString()),
                    ATTENDANCE.ATTENDANCE_DATE.between(params.from).and(params.to),
                    ATTENDANCE_CORRECT_EVENT.ATTENDANCE_CORRECT_EVENT_ID.isNull,
                )
            )
            .orderBy(ATTENDANCE.ATTENDANCE_DATE.asc())
            .fetchInto(ATTENDANCE_RECORD_EVENT)
            .groupBy { it.attendanceDate }
            .map { attendanceDateMap ->
                val startAt = attendanceDateMap.value.minOf { it.recordDatetime }
                AttendanceQueryResultItem(
                    attendanceDate = attendanceDateMap.key,
                    startAt = startAt,
                    endAt = attendanceDateMap.value.maxOf { it.recordDatetime }.takeUnless { it == startAt }
                )
            }
            .let {
                AttendanceQueryResult(
                    data = it
                )
            }
}
