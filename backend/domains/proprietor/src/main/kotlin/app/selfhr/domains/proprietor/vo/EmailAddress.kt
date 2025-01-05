package app.selfhr.domains.proprietor.vo

@JvmInline
value class EmailAddress(val value: String) {
    init {
        require(emailPattern.matches(value)) {
            "Email address format is invalid"
        }
    }

    companion object {
        private val emailPattern = Regex(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" +
                "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        )
    }
}
