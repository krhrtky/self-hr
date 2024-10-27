package com.example.infrastructure.users

import com.example.domains.entities.users.UserRepository
import com.example.infrastructure.db.tables.records.AppUserRecord
import com.example.infrastructure.db.tables.references.APP_USER
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class MySQLUserRepository(
    private val context: DSLContext,
) : UserRepository() {
    override fun find(id: String) =
        context
            .selectFrom(APP_USER)
            .where(
                APP_USER.ID.eq(id)
            )
            .fetchOneInto(APP_USER)
            ?.let {
                mapToEntity(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                )
            }

    override fun upsert(raw: UserRaw) {
        raw
            .let {
                AppUserRecord(
                    id = it.id,
                    firstName = it.firstName,
                    lastName = it.lastName,
                    email = it.email,
                )
            }
            .let {
                context
                    .insertInto(APP_USER)
                    .set(it)
                    .onDuplicateKeyUpdate()
                    .set(APP_USER.UPDATED_AT, OffsetDateTime.now())
                    .execute()
            }
    }
}
