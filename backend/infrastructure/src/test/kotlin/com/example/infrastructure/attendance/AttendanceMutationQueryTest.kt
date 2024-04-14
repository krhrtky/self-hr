package com.example.infrastructure.attendance

import com.example.infrastructure.db.tables.records.AttendanceCorrectEventRecord
import com.example.infrastructure.db.tables.records.AttendanceRecord
import com.example.infrastructure.db.tables.records.AttendanceRecordEventRecord
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttendanceMutationQueryTest {
    private val mutation: AttendanceMutationQuery = AttendanceMutationQuery(DSL.using(SQLDialect.POSTGRES))

    @BeforeAll
    fun beforeAll() {
        mockkStatic(LocalDateTime::class)
        every { LocalDateTime.now() } returns LocalDateTime.of(2024, 1, 7, 18, 49, 51, 803045)
    }

    @AfterAll
    fun afterAll() {
        clearAllMocks()
    }

    @Test
    fun upsertAttendance() {
        val attendance = AttendanceRecord(
            attendanceId = "aff71e0c-9c33-47fb-ad97-77dcc81a1bdf",
            userId = "8e53511f-d539-4ccb-a66d-11d816c44ec0",
            attendanceDate = LocalDate.of(2024, 1, 6),
        )
        val queryString = mutation.upsertAttendance(attendance)
        val expected = """
              insert into "ATTENDANCE" ("ATTENDANCE_ID", "USER_ID", "ATTENDANCE_DATE")
              values (
                'aff71e0c-9c33-47fb-ad97-77dcc81a1bdf', 
                '8e53511f-d539-4ccb-a66d-11d816c44ec0', 
                date '2024-01-06'
              )
              on conflict ("ATTENDANCE_ID")
              do update
              set
                "UPDATED_AT" = timestamp '2024-01-07 18:49:51.000803045'
        """.trimIndent()

        assertThat(queryString.toString()).isEqualTo(expected)
    }

    @Test
    fun upsertAttendanceRecordEvent() {
        val event = AttendanceRecordEventRecord(
            attendanceRecordEventId = "8e53511f-d539-4ccb-a66d-11d816c44ec0",
            attendanceId = "aff71e0c-9c33-47fb-ad97-77dcc81a1bdf",
            attendanceDate = LocalDate.of(2024, 1, 6),
            recordDatetime = OffsetDateTime.of(2023, 12, 15, 1, 2, 3, 4, ZoneOffset.of("+09:00:00")),
            eventCreatedDatetime = OffsetDateTime.of(2023, 12, 16, 2, 3, 4, 5, ZoneOffset.of("+09:00:00"))
        )

        val queryString = mutation.upsertEvent(event)

        val expected = """
              insert into "ATTENDANCE_RECORD_EVENT" (
                "ATTENDANCE_RECORD_EVENT_ID",
                "ATTENDANCE_ID",
                "ATTENDANCE_DATE",
                "RECORD_DATETIME",
                "EVENT_CREATED_DATETIME"
              )
              values (
                '8e53511f-d539-4ccb-a66d-11d816c44ec0', 
                'aff71e0c-9c33-47fb-ad97-77dcc81a1bdf', 
                date '2024-01-06', 
                timestamp '2023-12-15 01:02:03.000000004', 
                timestamp '2023-12-16 02:03:04.000000005'
              )
              on conflict ("ATTENDANCE_RECORD_EVENT_ID")
              do update
              set
                "UPDATED_AT" = timestamp '2024-01-07 18:49:51.000803045'
        """.trimIndent()
        assertThat(queryString.toString()).isEqualTo(expected)
    }

    @Test
    fun upsertAttendanceCorrectEvent() {
        val event = AttendanceCorrectEventRecord(
            attendanceCorrectEventId = "8e53511f-d539-4ccb-a66d-11d816c44ec0",
            attendanceId = "aff71e0c-9c33-47fb-ad97-77dcc81a1bdf",
            attendanceRecordEventId = "19be96b3-9405-43d0-8e3b-a70b3ecbdb14",
            attendanceDate = LocalDate.of(2024, 1, 6),
            correctDatetime = OffsetDateTime.of(2023, 12, 15, 1, 2, 3, 4, ZoneOffset.of("+09:00:00")),
            eventCreatedDatetime = OffsetDateTime.of(2023, 12, 16, 2, 3, 4, 5, ZoneOffset.of("+09:00:00"))
        )

        val queryString = mutation.upsertEvent(event)

        val expected = """
              insert into "ATTENDANCE_CORRECT_EVENT" (
                "ATTENDANCE_CORRECT_EVENT_ID",
                "ATTENDANCE_ID",
                "ATTENDANCE_RECORD_EVENT_ID",
                "ATTENDANCE_DATE",
                "CORRECT_DATETIME",
                "EVENT_CREATED_DATETIME"
              )
              values (
                '8e53511f-d539-4ccb-a66d-11d816c44ec0', 
                'aff71e0c-9c33-47fb-ad97-77dcc81a1bdf', 
                '19be96b3-9405-43d0-8e3b-a70b3ecbdb14', 
                date '2024-01-06', 
                timestamp '2023-12-15 01:02:03.000000004', 
                timestamp '2023-12-16 02:03:04.000000005'
              )
              on conflict ("ATTENDANCE_CORRECT_EVENT_ID")
              do update
              set
                "UPDATED_AT" = timestamp '2024-01-07 18:49:51.000803045'
        """.trimIndent()
        assertThat(queryString.toString()).isEqualTo(expected)
    }
}
