package com.example.domains.entities.users

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import java.util.UUID

class UserTest : DescribeSpec({
    beforeSpec {
        val mockedId = UUID.fromString("2467240f-5d27-4e42-946e-397509a74b7a")
        mockkStatic(UUID::class)
        every { UUID.randomUUID() } returns mockedId
    }

    afterSpec {
        clearAllMocks()
    }

    describe(".changeName") {
        val user = User.create(
            firstName = "test",
            lastName = "user",
            email = "test.user@example.com"
        )
        it("change name property value") {
            val changed = user.changeName("rename", "user2")

            assertSoftly(changed) {
                firstName shouldBe "rename"
                lastName shouldBe "user2"
            }
        }
        it("not change email") {
            val changed = user.changeName("rename", "user2")

            assertSoftly(changed) {
                email shouldBe "test.user@example.com"
            }
        }
    }
    describe(".getEvent") {
        it("returns events when called for the first time") {
            val user = User.create(
                firstName = "test",
                lastName = "user",
                email = "test.user@example.com"
            )
            // A newly created user should have UserCreatedEvent
            val initialEvents = user.getEvent()
            initialEvents.size shouldBe 1
            initialEvents.first() shouldBe UserCreatedEvent(user.id.value, user.email)
        }

        it("throws EventAlreadyConsumedException on subsequent calls") {
            val user = User.create(
                firstName = "test",
                lastName = "user",
                email = "test.user@example.com"
            )
            user.getEvent() // First call consumes the initial UserCreatedEvent

            // Subsequent call should throw
            org.junit.jupiter.api.assertThrows<User.EventAlreadyConsumedException> {
                user.getEvent()
            }
        }

        it("returns multiple events if they occurred before first getEvent call") {
            var user = User.create(
                firstName = "test",
                lastName = "user",
                email = "test.user@example.com"
            )
            user = user.changeEmail("new.email@example.com") // This adds UserEmailUpdatedEvent

            val events = user.getEvent()
            events.size shouldBe 2
            events shouldContain UserCreatedEvent(user.id.value, "test.user@example.com") // Original email
            events shouldContain UserEmailUpdatedEvent(user.id.value, "test.user@example.com", "new.email@example.com")

            // Subsequent call should throw
            org.junit.jupiter.api.assertThrows<User.EventAlreadyConsumedException> {
                user.getEvent()
            }
        }

        it("returns an empty list when no events are present") {
            val user = User.create(
                firstName = "test",
                lastName = "user",
                email = "test.user@example.com"
            )
            // Use fromRepository to create a user with no initial events
            val userWithNoEvents = User.fromRepository(
                id = UUID.randomUUID().toString(),
                firstName = "test",
                lastName = "user",
                email = "no.events@example.com"
            )

            val events = userWithNoEvents.getEvent()
            events.isEmpty() shouldBe true
        }

        it("throws EventAlreadyConsumedException on subsequent calls when no events were present initially") {
            val user = User.create(
                firstName = "test",
                lastName = "user",
                email = "test.user@example.com"
            )
            // Use fromRepository to create a user with no initial events
            val userWithNoEvents = User.fromRepository(
                id = UUID.randomUUID().toString(),
                firstName = "test",
                lastName = "user",
                email = "no.events@example.com"
            )

            userWithNoEvents.getEvent() // First call (returns empty list and consumes)

            org.junit.jupiter.api.assertThrows<User.EventAlreadyConsumedException> {
                userWithNoEvents.getEvent() // Subsequent call
            }
        }
    }
    describe(".changeEmail") {
        val user = User.create(
            firstName = "test",
            lastName = "user",
            email = "test.user@example.com"
        )
        it("change email property value") {
            val changed = user.changeEmail("rename.user@example.com")

            assertSoftly(changed) {
                email shouldBe "rename.user@example.com"
            }
        }
        it("not change name") {
            val changed = user.changeEmail("rename.user@example.com")

            assertSoftly(changed) {
                firstName shouldBe "test"
                lastName shouldBe "user"
            }
        }
        it("occurred event") {
            val changed = user.changeEmail("rename.user@example.com")
            val event = changed.getEvent()

            event should {
                it shouldContain UserEmailUpdatedEvent(
                    userId = "2467240f-5d27-4e42-946e-397509a74b7a",
                    beforeEmail = "test.user@example.com",
                    afterEmail = "rename.user@example.com"
                )
            }
        }
    }
})
