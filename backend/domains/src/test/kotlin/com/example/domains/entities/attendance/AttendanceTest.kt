package com.example.domains.entities.attendance

import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.attendance.events.AttendanceEventID
import com.example.domains.entities.users.UserID
import com.github.michaelbull.result.Ok
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Timestamp
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttendanceTest {
    private val id = AttendanceID(UUID.fromString("8fd498cb-0436-4c43-48a1-99887164778e"))
    private val userId = UserID(UUID.fromString("1b85db5f-09b0-3ae0-d948-e0a273c8d35c"))
    private val today = LocalDate.now()

    @BeforeAll
    fun beforeAll() {
        val mockedId = UUID.fromString("2467240f-5d27-4e42-946e-397509a74b7a")
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns mockedId
        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now() } returns OffsetDateTime.of(2023, 12, 2, 3, 4, 5, 6, ZoneOffset.ofHours(9))
    }

    @AfterAll
    fun afterAll() {
        clearAllMocks()
    }

    @Nested
    inner class CreateTests {
        @Test
        fun `Attendance creation with proper ID, User ID and attendance date`() {
            val attendance = Attendance.create(userId, today)

            assertEquals(userId, attendance.userId, "User ID should match")
            assertEquals(today, attendance.attendanceDate, "Attendance date should match")
            assertTrue(attendance.attendanceRecords.isEmpty(), "Initial attendance records should be empty")
        }
    }

    @Nested
    inner class RecordTests {
        private val registrationTime = OffsetDateTime.now()

        @Test
        fun `Recording attendance with proper timestamp`() {
            val today = LocalDate.now()
            val createdAttendance = Attendance.create(userId, today)

            val (result, error) = createdAttendance.record(registrationTime)

            assertThat(error).isNull()
            val (attendance, _) = checkNotNull(result)

            assertThat(attendance.attendanceRecords).size().isEqualTo(1)
            assertThat(attendance.attendanceRecords).isEqualTo(
                listOf(
                    AttendanceEvent.TimeRecordingEvent(
                        id = AttendanceEventID(UUID.fromString("2467240f-5d27-4e42-946e-397509a74b7a")),
                        recordAt = registrationTime,
                        attendanceDate = today,
                        timestamp = OffsetDateTime
                            .of(2023, 12, 2, 3, 4, 5, 6, ZoneOffset.ofHours(9))
                            .toInstant().let(Timestamp::from),
                    )
                )
            )
        }
    }

    @Nested
    inner class CorrectTests {
        private val userId = UserID(UUID.randomUUID())
        private val today = LocalDate.now()
        private val createdAttendance = Attendance.create(userId, today)
        private val registrationTime = OffsetDateTime.now()

        @Test
        fun `Correction of registered attendance`() {
            val result = createdAttendance.record(registrationTime)

            assertTrue(result is Ok, "Expected Result.Ok")

            val (attendanceWithRecord, event) = result.value

            val correctionResult = attendanceWithRecord.correct(event.id, registrationTime.plusHours(1))

            assertTrue(correctionResult is Ok, "Expected Result.Ok")

            val (correctedAttendance, correctionEvent) = correctionResult.value

            assertThat(correctionEvent).isInstanceOf(AttendanceEvent.TimeCorrectionEvent::class.java)
            assert(correctionEvent is AttendanceEvent.TimeCorrectionEvent)

            assertEquals(
                2,
                correctedAttendance.attendanceRecords.size,
                "Corrected attendance records should contain two records"
            )
        }
    }
}
