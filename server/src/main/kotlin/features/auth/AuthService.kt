package com.features.auth

import java.util.UUID

class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
) {
    fun register(
        email: String,
        displayName: String,
        password: String,
    ): Result<UserSession> {
        if (userRepository.findByEmail(email) != null) {
            return Result.failure(IllegalStateException("email_in_use"))
        }

        val hash = passwordHasher.hash(password)
        val user = userRepository.create(email, displayName, hash)
        return Result.success(UserSession(user.id.value.toString(), user.email))
    }

    fun login(
        email: String,
        password: String,
    ): Result<UserSession> {
        val user =
            userRepository.findByEmail(email)
                ?: return Result.failure(IllegalArgumentException("invalid_credentials"))
        if (!user.isActive) return Result.failure(IllegalStateException("inactive_user"))
        if (!passwordHasher.verify(password, user.password)) {
            return Result.failure(IllegalArgumentException("invalid_credentials"))
        }
        return Result.success(UserSession(user.id.value.toString(), user.email))
    }

    fun getSession(userId: String): UserSession? {
        val uuid = runCatching { UUID.fromString(userId) }.getOrNull() ?: return null
        return userRepository.findById(uuid)?.let {
            UserSession(it.id.value.toString(), it.email)
        }
    }
}
