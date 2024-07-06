package com.example.applications.attendance

import com.example.domains.entities.attendance.Attendance
import com.example.domains.entities.attendance.AttendanceException
import com.example.domains.entities.attendance.AttendanceException.AttendanceNotExistsException
import com.example.domains.entities.attendance.AttendanceRepository
import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.users.UserID
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.flatMap
import com.github.michaelbull.result.map
import com.github.michaelbull.result.onSuccess
import com.github.michaelbull.result.toResultOr
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.util.UUID

@Service
class AttendanceApplicationService(
    private val repository: AttendanceRepository,
) {
    fun <T> record(recordDTO: RecordDTO, mapper: (Result<AttendanceEvent, AttendanceException>) -> T): T {
        val userId = UserID.fromString(recordDTO.userId)
        val attendance = repository
            .find(userId, recordDTO.recordTime.toLocalDate())
            ?: Attendance.create(userId, recordDTO.recordTime.toLocalDate())

        return attendance
            .record(recordDTO.recordTime)
            .onSuccess(::saveAttendance)
            .map { it.second }
            .let(mapper)
    }

    @Transactional
    fun correct(correctDTO: CorrectDTO): Result<AttendanceEvent, AttendanceException> {
        val correctTargetEventID = correctDTO.correctEventID
            .let(UUID::fromString)
            .let(::AttendanceEventID)

        return repository
            .find(correctTargetEventID)
            .toResultOr {
                AttendanceNotExistsException("AttendanceEventID(${correctDTO.correctEventID}) does not exists.")
            }
            .flatMap {
                it
                    .correct(
                        correctTarget = correctTargetEventID,
                        correctDateTime = correctDTO.correctDateTime,
                    )
            }
            .onSuccess(::saveAttendance)
            .map { it.second }
    }

    private fun saveAttendance(result: Pair<Attendance, AttendanceEvent>) =
        repository.save(result.first)

    data class RecordDTO(
        val userId: String,
        val recordTime: OffsetDateTime,
    )

    data class CorrectDTO(
        val correctEventID: String,
        val correctDateTime: OffsetDateTime,
    )
}
