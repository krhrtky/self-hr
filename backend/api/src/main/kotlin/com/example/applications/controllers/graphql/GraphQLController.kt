package com.example.applications.controllers.graphql

import com.example.applications.graphql.types.CreateUserInput
import com.example.applications.graphql.types.User
import com.example.applications.users.UserApplicationService
import com.example.applications.users.UserCreateInput
import com.example.domains.entities.users.UserQueryService
import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.DgsQuery
import com.netflix.graphql.dgs.InputArgument

@DgsComponent
class GraphQLController(
    private val service: UserApplicationService,
    private val queryService: UserQueryService,
) {
    @DgsQuery
    fun fetchUser(@InputArgument id: String): User? =
        queryService
            .find(id)
            ?.let {
                User(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                )
            }

    @DgsMutation
    fun createUser(@InputArgument input: CreateUserInput): User =
        UserCreateInput(
            firstName = input.firstName,
            lastName = input.lastName,
            email = input.email,
        )
            .let(service::create)
            .map {
                User(
                    id = it,
                    firstName = input.firstName,
                    lastName = input.lastName,
                    email = input.email,
                )
            }
            .getOrThrow()
}
