package com.example.infrastructure.users

import com.example.domains.entities.users.AllUsersCondition
import com.example.domains.entities.users.UserDTO
import com.example.domains.entities.users.UserQueryService
import com.example.domains.entities.users.Users
import com.example.infrastructure.db.tables.references.APP_USER
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class MySQLUserQueryService(
    private val context: DSLContext,
) : UserQueryService {
    override fun find(id: String): UserDTO? =
        context
            .selectFrom(APP_USER)
            .where(
                APP_USER.ID.eq(id)
            )
            .fetchOne {
                UserDTO(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                )
            }
    override fun allUsers(condition: AllUsersCondition): Users {
        val total = context.fetchCount(APP_USER)
        val users = context
            .selectFrom(APP_USER)
            .orderBy(APP_USER.ID.desc())
            .limit(condition.limit)
            .offset(condition.offset)
            .fetchInto(APP_USER)
            .map {
                UserDTO(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                )
            }
        return Users(
            total = total.toLong(),
            users = users,
        )
    }
}
