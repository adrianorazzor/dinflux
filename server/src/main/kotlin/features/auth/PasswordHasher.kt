package com.features.auth

import org.mindrot.jbcrypt.BCrypt

class PasswordHasher {
    fun hash(raw: String): String = BCrypt.hashpw(raw, BCrypt.gensalt())

    fun verify(
        raw: String,
        hash: String,
    ): Boolean = BCrypt.checkpw(raw, hash)
}
