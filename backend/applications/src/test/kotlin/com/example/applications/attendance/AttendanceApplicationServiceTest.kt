package com.example.applications.attendance

import com.example.domains.entities.attendance.Attendance
import com.example.domains.entities.attendance.AttendanceException.AttendanceNotExistsException
import com.example.domains.entities.attendance.AttendanceException.CorrectTargetDoesNotExistsException
import com.example.domains.entities.attendance.AttendanceID
import com.example.domains.entities.attendance.AttendanceRepository
import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.attendance.inject
import com.example.domains.entities.users.UserID
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.util.UUID

class AttendanceApplicationServiceTest {
    private val mockedRepository = mockk<AttendanceRepository>()
    private val service = AttendanceApplicationService(
        repository = mockedRepository
    )
    private val userId = UserID.fromString("d02e57e6-7a65-47b7-918e-3c480bba7639")
    private val recordTime = ZonedDateTime.of(2023, 12, 15, 1, 2, 3, 4, ZoneId.of("Asia/Tokyo")).toOffsetDateTime()

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Nested
    inner class Record {
        @Test
        fun recordCompleteWhenAttendanceDoesNotExists() {
            every { mockedRepository.find(userId, recordTime.toLocalDate()) } returns null
            justRun { mockedRepository.save(any<Attendance>()) }

            val result = service
                .record(
                    AttendanceApplicationService.RecordDTO(
                        userId = "d02e57e6-7a65-47b7-918e-3c480bba7639",
                        recordTime = recordTime,
                    )
                ) {
                    it
                }

            val expect = AttendanceEvent.TimeRecordingEvent(
                id = AttendanceEventID(UUID.randomUUID()),
                attendanceDate = LocalDate.of(2023, 12, 15),
                timestamp = Timestamp.valueOf(LocalDateTime.now()),
                recordAt = recordTime,
            )

            result.shouldBeInstanceOf<Ok<AttendanceEvent>>()
                .value.shouldBeEqualToIgnoringFields(
                    expect,
                    AttendanceEvent::id,
                    AttendanceEvent::timestamp,
                )

            verify(exactly = 1) { mockedRepository.save(any<Attendance>()) }
        }

        @Test
        fun recordCompleteWhenAttendanceExists() {
            every {
                mockedRepository.find(userId, recordTime.toLocalDate())
            } returns Attendance.create(userId, attendanceDate = LocalDate.of(2023, 12, 15))
            justRun { mockedRepository.save(any<Attendance>()) }

            val result = service
                .record(
                    AttendanceApplicationService.RecordDTO(
                        userId = "d02e57e6-7a65-47b7-918e-3c480bba7639",
                        recordTime = recordTime,
                    )
                ) {
                    it
                }

            val expect = AttendanceEvent.TimeRecordingEvent(
                id = AttendanceEventID(UUID.randomUUID()),
                attendanceDate = LocalDate.of(2023, 12, 15),
                timestamp = Timestamp.valueOf(LocalDateTime.now()),
                recordAt = recordTime,
            )

            result.shouldBeInstanceOf<Ok<AttendanceEvent>>()
                .value.shouldBeEqualToIgnoringFields(
                    expect,
                    AttendanceEvent::id,
                    AttendanceEvent::timestamp,
                )

            verify(exactly = 1) { mockedRepository.save(any<Attendance>()) }
        }
    }

    @Nested
    inner class Correct {
        private val attendanceID = AttendanceID(UUID.fromString("e9e0f461-c812-4e49-a515-a36d2b8e669b"))
        private val attendanceEventID = AttendanceEventID(UUID.fromString("cbee3aa9-af79-4ab2-8f2c-86dee4ff190d"))
        private val recordDateTime = OffsetDateTime.of(2023, 12, 15, 1, 2, 3, 4, ZoneOffset.of("+09:00:00"))
        private val timestamp = LocalDateTime.of(2023, 12, 15, 1, 2, 3, 4).let(Timestamp::valueOf)

        @Test
        fun correctionIsSussedWhenAttendanceAndCorrectTargetExists() {
            val attendance = Attendance.inject(
                attendanceID,
                userId,
                LocalDate.of(2023, 12, 15),
                listOf(
                    AttendanceEvent.TimeRecordingEvent(
                        attendanceEventID,
                        LocalDate.of(2023, 12, 15),
                        Timestamp.valueOf(LocalDateTime.now()),
                        recordDateTime,
                    )
                )
            )

            every {
                mockedRepository.find(AttendanceEventID(UUID.fromString("cbee3aa9-af79-4ab2-8f2c-86dee4ff190d")))
            } returns attendance
            justRun { mockedRepository.save(any<Attendance>()) }

            val result = AttendanceApplicationService.CorrectDTO(
                correctEventID = "cbee3aa9-af79-4ab2-8f2c-86dee4ff190d",
                correctDateTime = OffsetDateTime.of(2023, 12, 16, 2, 3, 4, 5, ZoneOffset.of("+09:00:00"))
            )
                .let(service::correct)

            val expected = AttendanceEvent.TimeCorrectionEvent(
                id = AttendanceEventID(UUID.randomUUID()),
                attendanceDate = LocalDate.of(2023, 12, 15),
                timestamp = timestamp,
                correctAttendanceEventID = "cbee3aa9-af79-4ab2-8f2c-86dee4ff190d"
                    .let(UUID::fromString)
                    .let(::AttendanceEventID),
                correctDateTime = OffsetDateTime.of(2023, 12, 16, 2, 3, 4, 5, ZoneOffset.of("+09:00:00")),
            )

            result.shouldBeInstanceOf<Ok<AttendanceEvent.TimeCorrectionEvent>>()
                .value.shouldBeEqualToIgnoringFields(
                    expected,
                    AttendanceEvent::id,
                    AttendanceEvent::timestamp,
                )

            verify(exactly = 1) { mockedRepository.save(any<Attendance>()) }
        }

        @Test
        fun correctionIsFailedWhenTargetAttendanceEventDoesNotExists() {
            val attendance = Attendance.inject(
                attendanceID,
                userId,
                LocalDate.of(2023, 12, 15),
                listOf(
                    AttendanceEvent.TimeRecordingEvent(
                        AttendanceEventID(UUID.fromString("ba135a57-4301-4577-a8ae-4daf8342402e")),
                        LocalDate.of(2023, 12, 15),
                        Timestamp.valueOf(LocalDateTime.now()),
                        recordDateTime,
                    )
                )
            )

            every {
                mockedRepository.find(AttendanceEventID(UUID.fromString("cbee3aa9-af79-4ab2-8f2c-86dee4ff190d")))
            } returns attendance

            val result = AttendanceApplicationService.CorrectDTO(
                correctEventID = "cbee3aa9-af79-4ab2-8f2c-86dee4ff190d",
                correctDateTime = OffsetDateTime.of(2023, 12, 16, 2, 3, 4, 5, ZoneOffset.of("+09:00:00"))
            )
                .let(service::correct)

            result.shouldBeInstanceOf<Err<CorrectTargetDoesNotExistsException>>()
            verify(exactly = 0) { mockedRepository.save(any<Attendance>()) }
        }

        @Test
        fun correctionIsFailedWhenAttendanceDoesNotExists() {
            every {
                mockedRepository.find(AttendanceEventID(UUID.fromString("cbee3aa9-af79-4ab2-8f2c-86dee4ff190d")))
            } returns null

            val result = AttendanceApplicationService.CorrectDTO(
                correctEventID = "cbee3aa9-af79-4ab2-8f2c-86dee4ff190d",
                correctDateTime = OffsetDateTime.of(2023, 12, 16, 2, 3, 4, 5, ZoneOffset.of("+09:00:00"))
            )
                .let(service::correct)

            result.shouldBeInstanceOf<Err<AttendanceNotExistsException>>()
            verify(exactly = 0) { mockedRepository.save(any<Attendance>()) }
        }
    }
}
