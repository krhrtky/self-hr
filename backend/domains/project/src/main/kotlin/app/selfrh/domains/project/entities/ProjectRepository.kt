package app.selfrh.domains.project.entities

import app.selfhr.domains.proprietor.vo.ProprietorID
import app.selfrh.domains.project.vo.ProjectID

interface ProjectRepository {
    fun save(project: Project)
    fun find(id: ProjectID): Project?
    fun find(proprietorID: ProprietorID): List<Project>
}
