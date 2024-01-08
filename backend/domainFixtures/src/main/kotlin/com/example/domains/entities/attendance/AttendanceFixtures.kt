package com.example.domains.entities.attendance

import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.users.UserID
import java.time.LocalDate
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

fun Attendance.Companion.inject(
    id: AttendanceID,
    userId: UserID,
    attendanceDate: LocalDate,
    attendanceRecords: List<AttendanceEvent>,
): Attendance =
    Attendance::class.primaryConstructor!!
        .apply {
            this.isAccessible = true
        }
        .call(
            id,
            userId,
            attendanceDate,
            attendanceRecords,
        )
