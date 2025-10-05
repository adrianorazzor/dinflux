package com.models

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.dao.UUIDEntity
import org.jetbrains.exposed.v1.dao.UUIDEntityClass
import org.jetbrains.exposed.v1.datetime.CurrentDateTime
import org.jetbrains.exposed.v1.datetime.datetime
import java.util.UUID

const val MAX_VARCHAR_LENGTH = 255

object Users : UUIDTable() {
    val email = varchar("email", length = MAX_VARCHAR_LENGTH).uniqueIndex()
    val passwordHash = varchar("password_hash", length = MAX_VARCHAR_LENGTH)
    val isActive = bool("is_active")
    val displayName = varchar("display_name", length = MAX_VARCHAR_LENGTH)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}

@Suppress("unused")
class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var email by Users.email
    var password by Users.passwordHash
    var createdAt by Users.createdAt
    var isActive by Users.isActive
    var displayName by Users.displayName
}
