package app.selfhr.domains.contract.vo

@JvmInline
value class ContractVersion internal constructor(val value: Int) {
    init {
        require(value >= 0) { "Contract version must be greater than zero." }
    }

    fun bumpup() = ContractVersion(value.inc())
    companion object {
        val FIRST = ContractVersion(1)
    }
}
