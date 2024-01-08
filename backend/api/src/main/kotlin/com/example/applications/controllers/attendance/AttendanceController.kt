package com.example.applications.controllers.attendance

import com.example.applications.attendance.AttendanceApplicationService
import com.example.domains.entities.attendance.AttendanceException
import com.github.michaelbull.result.fold
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.of
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import java.time.ZoneOffset

@RestController
@RequestMapping("/attendance")
class AttendanceController(
    private val service: AttendanceApplicationService,
) {
    @PostMapping("/record")
    fun record(@RequestBody requestBody: RecordRequestBody) =
        AttendanceApplicationService.RecordDTO(
            requestBody.userId,
            requestBody.recordTime.atOffset(ZoneOffset.of("+09:00:00"))
        )
            .let {
                service.record(it) { result ->
                    result
                        .fold(
                            success = { event -> RecordSuccess(event.id.value.toString()).let(::ok) },
                            failure = ::mapToErrorResponse
                        )
                }
            }

    @PostMapping("/correct")
    fun correct(@RequestBody requestBody: CorrectRequestBody) =
        AttendanceApplicationService.CorrectDTO(
            requestBody.attendanceID,
            requestBody.correctEventID,
            requestBody.correctDateTime.atOffset(ZoneOffset.of("+09:00:00"))
        )
            .let {
                service
                    .correct(it)
                    .fold(
                        success = { event -> CorrectSuccess(event.id.value.toString()).let(::ok) },
                        failure = ::mapToErrorResponse
                    )
            }
}

data class RecordRequestBody(
    val userId: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val recordTime: LocalDateTime,
)

data class RecordSuccess(
    val id: String,
)

data class CorrectRequestBody(
    val attendanceID: String,
    val correctEventID: String,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val correctDateTime: LocalDateTime,
)
data class CorrectSuccess(
    val id: String,
)

private fun <T> mapToErrorResponse(exception: AttendanceException): ResponseEntity<T> =
    when (exception) {
        is AttendanceException.AttendanceNotExistsException -> HttpStatus.NOT_FOUND
        is AttendanceException.CorrectTargetDoesNotExistsException ->
            HttpStatus.INTERNAL_SERVER_ERROR
    }
        .let { httpStatus ->
            ProblemDetail
                .forStatusAndDetail(httpStatus, exception.message ?: "")
                .let(::of)
                .build()
        }
