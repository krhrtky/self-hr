package app.selfrh.domains.project.entities

import app.selfhr.domains.proprietor.vo.ProprietorID
import app.selfhr.shared.entity.Entity
import app.selfrh.domains.project.vo.ProjectID

class Project(
    override val id: ProjectID,
    val proprietorID: ProprietorID

) : Entity<ProjectID>
