package app.selfhr.domains.attendance.events

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class AttendanceEventIDTest {
    @Test
    fun `generate should create unique IDs`() {
        val id1 = AttendanceEventID.generate()
        val id2 = AttendanceEventID.generate()

        assertThat(id1).isNotNull()
        assertThat(id2).isNotNull()
        assertThat(id1).isNotEqualTo(id2)
        assertThat(id1.value).isInstanceOf(UUID::class.java)
        assertThat(id2.value).isInstanceOf(UUID::class.java)
    }

    @Test
    fun `generated ID should be time-ordered`() {
        val id1 = AttendanceEventID.generate()
        Thread.sleep(10) // Ensure some time passes
        val id2 = AttendanceEventID.generate()

        // UUIDv7 is time-ordered, so comparing as strings should show the order
        assertThat(id1.value.toString())
            .isLessThan(id2.value.toString())
    }
}
