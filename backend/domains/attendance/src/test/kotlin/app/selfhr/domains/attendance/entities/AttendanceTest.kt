package app.selfhr.domains.attendance.entities
import app.selfhr.domains.attendance.TestHelper
import app.selfhr.domains.attendance.events.AttendanceEvent
import app.selfhr.domains.attendance.events.AttendanceEventID
import java.time.LocalDate
import java.time.OffsetDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AttendanceTest {
    private val contractId = TestHelper.createContractId()
    private val attendanceDate = LocalDate.now()

    @Test
    fun `create should initialize attendance with empty records`() {
        val attendance = Attendance.create(
            contractId = contractId,
            attendanceDate = attendanceDate
        )

        assertThat(attendance.id).isNotNull()
        assertThat(attendance.contractId).isEqualTo(contractId)
        assertThat(attendance.attendanceDate).isEqualTo(attendanceDate)
        assertThat(attendance.attendanceRecords).isEmpty()
    }

    @Test
    fun `record should add time recording event`() {
        val attendance = Attendance.create(contractId, attendanceDate)
        val recordTime = OffsetDateTime.now()

        val updatedAttendance = attendance.record(recordTime)

        assertThat(updatedAttendance.attendanceRecords).hasSize(1)
        val event = updatedAttendance.attendanceRecords.first() as AttendanceEvent.TimeRecordingEvent
        assertThat(event.recordAt).isEqualTo(recordTime)
        assertThat(event.attendanceDate).isEqualTo(attendanceDate)
    }

    @Test
    fun `correct should add time correction event`() {
        val attendance = Attendance.create(contractId, attendanceDate)
        val recordTime = OffsetDateTime.now()
        val withRecord = attendance.record(recordTime)
        val correctTime = recordTime.plusHours(1)

        val correctedAttendance = withRecord.correct(
            withRecord.attendanceRecords.first().id,
            correctTime
        )

        assertThat(correctedAttendance.attendanceRecords).hasSize(2)
        val correctionEvent = correctedAttendance.attendanceRecords.last() as AttendanceEvent.TimeCorrectionEvent
        assertThat(correctionEvent.correctDateTime).isEqualTo(correctTime)
        assertThat(correctionEvent.correctAttendanceEventID).isEqualTo(withRecord.attendanceRecords.first().id)
    }

    @Test
    fun `correct should throw exception when target event does not exist`() {
        val attendance = Attendance.create(contractId, attendanceDate)
        val nonExistentEventId = AttendanceEventID.generate()

        assertThrows<AttendanceException.CorrectTargetDoesNotExistsException> {
            attendance.correct(nonExistentEventId, OffsetDateTime.now())
        }
    }
}
