package com.example.applications.users

import com.example.domains.entities.users.User
import com.example.domains.entities.users.UserQueryService
import com.example.domains.entities.users.UserRepository
import com.github.michaelbull.result.toResultOr
import org.springframework.stereotype.Service

@Service
class UserApplicationService(
    private val repository: UserRepository,
    private val queryService: UserQueryService,
) {
    fun find(id: String) = queryService
        .find(id)
        .toResultOr {
            UserDoesNotFindException("User(id = $id) does not exists.")
        }

    fun create(input: UserCreateInput) = {
        input
            .let {
                User.create(input.firstName, input.lastName, input.email)
            }
            .apply(repository::save).id.value
    }
        .let(::runCatching)
}

class UserDoesNotFindException(override val message: String?) : Exception(message)

data class UserCreateInput(
    val firstName: String,
    val lastName: String,
    val email: String,
)
