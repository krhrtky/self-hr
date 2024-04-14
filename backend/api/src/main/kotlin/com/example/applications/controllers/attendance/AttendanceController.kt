package com.example.applications.controllers.attendance

import com.example.applications.attendance.AttendanceApplicationService
import com.example.domains.entities.attendance.AttendanceException
import com.example.domains.entities.attendance.AttendanceQueryService
import com.example.domains.entities.attendance.AttendanceSearchParameters
import com.github.michaelbull.result.fold
import jakarta.servlet.http.HttpServletRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.of
import org.springframework.http.ResponseEntity.ok
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@RestController
@RequestMapping("/attendance")
class AttendanceController(
    private val service: AttendanceApplicationService,
    private val queryService: AttendanceQueryService,
) {
    @PostMapping("/record")
    fun record(
        request: HttpServletRequest,
        @RequestBody requestBody: RecordRequestBody
    ) =
        AttendanceApplicationService.RecordDTO(
            request.getAttribute("userId").toString(),
            requestBody.recordTime
                .atOffset(ZoneOffset.UTC)
                .atZoneSameInstant(ZoneOffset.of("+09:00:00"))
                .toOffsetDateTime()
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
            requestBody.correctEventID,
            requestBody.correctDateTime
                .atOffset(ZoneOffset.UTC)
                .atZoneSameInstant(ZoneOffset.of("+09:00:00"))
                .toOffsetDateTime()
        )
            .let(service::correct)
            .fold(
                success = { event -> CorrectSuccess(event.id.value.toString()).let(::ok) },
                failure = ::mapToErrorResponse
            )

    @GetMapping("/detail")
    fun detail(
        request: HttpServletRequest,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        from: LocalDate,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        to: LocalDate,
    ) = queryService
        .query(
            AttendanceSearchParameters(
                userId = request.getAttribute("userId").toString().let(UUID::fromString),
                from = from,
                to = to,
            )
        )
        .let(::ok)
}

data class RecordRequestBody(
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    val recordTime: LocalDateTime,
)

data class RecordSuccess(
    val id: String,
)

data class CorrectRequestBody(
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
