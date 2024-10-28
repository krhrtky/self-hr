package com.example.domains.entities.users

abstract class UserRepository {
    abstract fun find(id: String): User?
    protected abstract fun upsert(raw: UserRaw)
    fun save(user: User) {
        UserRaw(
            id = user.id.value,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email
        )
            .let(::upsert)
    }

    protected fun mapToEntity(
        id: String,
        firstName: String,
        lastName: String,
        email: String,
    ): User = User.fromRepository(
        id,
        firstName,
        lastName,
        email,
    )

    protected data class UserRaw(
        val id: String,
        val firstName: String,
        val lastName: String,
        val email: String,
    )
}
