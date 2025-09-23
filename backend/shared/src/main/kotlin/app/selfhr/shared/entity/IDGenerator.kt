package app.selfhr.shared.entity

interface IDGenerator<I : ID<*>> {
    fun generate(): I
}
