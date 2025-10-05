package com.features.auth

import com.models.User
import com.models.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID

class UserRepository {
    fun findByEmail(email: String): User? =
        transaction {
            User.find { Users.email eq email }.singleOrNull()
        }

    fun findById(id: UUID): User? = transaction { User.findById(id) }

    fun create(
        email: String,
        displayName: String,
        passwordHash: String,
    ): User =
        transaction {
            User.new {
                this.email = email
                this.displayName = displayName
                this.password = passwordHash
                this.isActive = true
            }
        }
}
