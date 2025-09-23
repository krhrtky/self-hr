package app.selfhr.domains.attendance.exceptions

sealed class AttendanceException(
    override val message: String?,
    override val cause: Throwable? = null
) : Exception(message, cause) {
    class CorrectTargetDoesNotExistsException(
        message: String?,
        cause: Throwable? = null
    ) : AttendanceException(message, cause)
}
