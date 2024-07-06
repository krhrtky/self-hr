package com.example.domains.entities.attendance

import com.example.domains.entities.attendance.events.AttendanceEvent
import com.example.domains.entities.users.UserID
import java.time.LocalDate

fun Attendance.Companion.inject(
    id: AttendanceID,
    userId: UserID,
    attendanceDate: LocalDate,
    attendanceRecords: List<AttendanceEvent>,
): Attendance =
    Attendance(
        id,
        userId,
        attendanceDate,
        attendanceRecords,
    )
