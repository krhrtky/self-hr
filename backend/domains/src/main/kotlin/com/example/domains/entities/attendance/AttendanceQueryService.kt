package com.example.domains.entities.attendance

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

interface AttendanceQueryService {
    fun query(params: AttendanceSearchParameters): AttendanceQueryResult
}

data class AttendanceSearchParameters(
    val userId: UUID,
    val from: LocalDate,
    val to: LocalDate,
)

data class AttendanceQueryResult(
    val data: List<AttendanceQueryResultItem>
)

data class AttendanceQueryResultItem(
    val attendanceDate: LocalDate,
    val startAt: OffsetDateTime,
    val endAt: OffsetDateTime?,
)
