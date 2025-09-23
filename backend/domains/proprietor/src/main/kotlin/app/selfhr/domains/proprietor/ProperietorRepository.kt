package app.selfhr.domains.proprietor

import app.selfhr.domains.proprietor.entities.Proprietor
import app.selfhr.domains.proprietor.vo.ProprietorID

interface ProperietorRepository {
    fun save(proprietor: Proprietor)
    fun find(id: ProprietorID): Proprietor
}
