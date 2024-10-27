package com.example.domains.entities.users

class User private constructor(
    val id: UserID,
    internal val firstName: String,
    internal val lastName: String,
    internal val email: String,
    private val event: List<UserDomainEvent>,
    private var eventConsumed: Boolean = false
) {
    fun changeName(firstName: String, lastName: String) =
        update(firstName = firstName, lastName = lastName)
    fun changeEmail(newEmail: String) =
        update(
            email = newEmail,
            event =
            UserEmailUpdatedEvent(
                id.value,
                email,
                newEmail,
            )
                .let(::listOf)
        )

    fun getEvent() =
        if (eventConsumed) {
            throw EventAlreadyConsumedException()
        } else {
            eventConsumed = false
            event
        }

    private fun update(
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        event: List<UserDomainEvent> = emptyList(),
    ) = User(
        id,
        firstName ?: this.firstName,
        lastName ?: this.lastName,
        email ?: this.email,
        this.event + event,
    )

    private class EventAlreadyConsumedException : Exception("Event has already consumed.")

    companion object {
        fun create(
            firstName: String,
            lastName: String,
            email: String,
        ) =
            UserID
                .create()
                .let {
                    User(
                        it,
                        firstName,
                        lastName,
                        email,
                        UserCreatedEvent(
                            it.value,
                            email,
                        )
                            .let(::listOf)
                    )
                }

        internal fun fromRepository(
            id: String,
            firstName: String,
            lastName: String,
            email: String,
        ) = User(
            UserID.fromRepository(id),
            firstName,
            lastName,
            email,
            emptyList(),
        )
    }
}

interface UserDomainEvent

data class UserCreatedEvent(
    val userId: String,
    val email: String,
) : UserDomainEvent

data class UserEmailUpdatedEvent(
    val userId: String,
    val beforeEmail: String,
    val afterEmail: String,
) : UserDomainEvent
