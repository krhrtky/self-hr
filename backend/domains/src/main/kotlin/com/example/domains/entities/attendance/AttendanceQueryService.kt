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

data class AttendanceEventDTO(
    val id: String,
    val time: OffsetDateTime,
)

data class AttendanceQueryResult(
    val data: List<AttendanceQueryResultItem>
)

data class AttendanceQueryResultItem(
    val attendanceDate: LocalDate,
    val startAt: AttendanceEventDTO,
    val endAt: AttendanceEventDTO?,
)
