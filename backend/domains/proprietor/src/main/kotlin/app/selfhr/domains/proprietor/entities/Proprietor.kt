package app.selfhr.domains.proprietor.entities

import app.selfhr.domains.proprietor.vo.EmailAddress
import app.selfhr.domains.proprietor.vo.ProprietorID
import app.selfhr.shared.entity.Entity

class Proprietor internal constructor(
    override val id: ProprietorID,
    internal val emailAddress: EmailAddress,
) : Entity<ProprietorID>
