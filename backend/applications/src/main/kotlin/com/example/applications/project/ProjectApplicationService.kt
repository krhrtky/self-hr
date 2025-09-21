package com.example.applications.project

import app.selfrh.domains.project.entities.ProjectFactory
import app.selfrh.domains.project.entities.ProjectFactory.ProjectCreateCommand
import app.selfrh.domains.project.entities.ProjectRepository
import org.springframework.stereotype.Service

@Service
class ProjectApplicationService(
    private val projectFactory: ProjectFactory,
    private val projectRepository: ProjectRepository,
) {
    fun create(createCommand: ProjectCreateCommand) {

        val project = projectFactory.create(createCommand)
    }
}
