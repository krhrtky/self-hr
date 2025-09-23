package app.selfhr.shared.entity

interface Entity<IDT : ID<*>> {
    val id: IDT
}
