package app.selfhr.domains.proprietor.vo

interface ProprietorIDGenerator {
    fun generate(): ProprietorID
    fun from(value: String): ProprietorID
}
