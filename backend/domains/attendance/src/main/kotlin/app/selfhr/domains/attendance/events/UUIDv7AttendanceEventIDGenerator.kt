package app.selfhr.domains.attendance.events

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class UUIDv7AttendanceEventIDGenerator : AttendanceEventIDGenerator {
    override fun generate(): AttendanceEventID = Generators
        .timeBasedGenerator()
        .generate()
        .let(::AttendanceEventID)
}
