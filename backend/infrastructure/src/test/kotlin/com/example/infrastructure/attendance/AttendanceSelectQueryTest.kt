package com.example.infrastructure.attendance

import com.example.domains.entities.attendance.AttendanceID
import com.example.domains.entities.users.UserID
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
import java.util.UUID
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AttendanceSelectQueryTest {
    private val query: AttendanceSelectQuery = AttendanceSelectQuery(DSL.using(SQLDialect.POSTGRES))

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
    fun findAttendanceByAttendanceID() {
        val queryString = query
            .findAttendanceBy(AttendanceID(UUID.fromString("aff71e0c-9c33-47fb-ad97-77dcc81a1bdf")))
        val expected = """
            select
              "ATTENDANCE"."ATTENDANCE_ID",
              "ATTENDANCE"."USER_ID",
              "ATTENDANCE"."ATTENDANCE_DATE",
              "ATTENDANCE"."CREATED_AT",
              "ATTENDANCE"."UPDATED_AT"
            from "ATTENDANCE"
            where "ATTENDANCE"."ATTENDANCE_ID" = 'aff71e0c-9c33-47fb-ad97-77dcc81a1bdf'
        """.trimIndent()

        assertThat(queryString.toString()).isEqualTo(expected)
    }

    @Test
    fun findAttendanceByUserIDAndAttendanceDate() {
        val queryString = query
            .findAttendanceBy(
                UserID.fromString("aff71e0c-9c33-47fb-ad97-77dcc81a1bdf"),
                LocalDate.of(2024, 1, 6)
            )
        val expected = """
            select
              "ATTENDANCE"."ATTENDANCE_ID",
              "ATTENDANCE"."USER_ID",
              "ATTENDANCE"."ATTENDANCE_DATE",
              "ATTENDANCE"."CREATED_AT",
              "ATTENDANCE"."UPDATED_AT"
            from "ATTENDANCE"
            where (
              "ATTENDANCE"."USER_ID" = 'aff71e0c-9c33-47fb-ad97-77dcc81a1bdf'
              and "ATTENDANCE"."ATTENDANCE_DATE" = date '2024-01-06'
            )
        """.trimIndent()

        assertThat(queryString.toString()).isEqualTo(expected)
    }

    @Test
    fun findEventsByAttendanceIDs() {
        val queryString = query.findEventsBy(
            listOf(
                AttendanceID(UUID.fromString("aff71e0c-9c33-47fb-ad97-77dcc81a1bdf")),
                AttendanceID(UUID.fromString("8e53511f-d539-4ccb-a66d-11d816c44ec0")),
            )
        )
        val expected = """
            select
              "ATTENDANCE_RECORD_EVENT"."ATTENDANCE_RECORD_EVENT_ID",
              "ATTENDANCE_RECORD_EVENT"."ATTENDANCE_ID",
              "ATTENDANCE_RECORD_EVENT"."ATTENDANCE_DATE",
              "ATTENDANCE_RECORD_EVENT"."RECORD_DATETIME",
              "ATTENDANCE_RECORD_EVENT"."EVENT_CREATED_DATETIME",
              "ATTENDANCE_RECORD_EVENT"."CREATED_AT",
              "ATTENDANCE_RECORD_EVENT"."UPDATED_AT",
              "ATTENDANCE_CORRECT_EVENT"."ATTENDANCE_CORRECT_EVENT_ID",
              "ATTENDANCE_CORRECT_EVENT"."ATTENDANCE_ID",
              "ATTENDANCE_CORRECT_EVENT"."ATTENDANCE_RECORD_EVENT_ID",
              "ATTENDANCE_CORRECT_EVENT"."ATTENDANCE_DATE",
              "ATTENDANCE_CORRECT_EVENT"."CORRECT_DATETIME",
              "ATTENDANCE_CORRECT_EVENT"."EVENT_CREATED_DATETIME",
              "ATTENDANCE_CORRECT_EVENT"."CREATED_AT",
              "ATTENDANCE_CORRECT_EVENT"."UPDATED_AT"
            from "ATTENDANCE_RECORD_EVENT"
              left outer join "ATTENDANCE_CORRECT_EVENT"
                using ("ATTENDANCE_RECORD_EVENT_ID")
            where "ATTENDANCE_RECORD_EVENT"."ATTENDANCE_ID" in (
              'aff71e0c-9c33-47fb-ad97-77dcc81a1bdf', '8e53511f-d539-4ccb-a66d-11d816c44ec0'
            )
        """.trimIndent()

        assertThat(queryString.toString()).isEqualTo(expected)
    }
}
