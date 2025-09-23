package app.selfhr.domains.attendance.vo

import com.fasterxml.uuid.Generators
import org.springframework.stereotype.Component

@Component
class UUIDv7AttendanceIDGenerator : AttendanceIDGenerator {
    override fun generate(): AttendanceID = Generators
        .timeBasedGenerator()
        .generate()
        .let(::AttendanceID)
}
