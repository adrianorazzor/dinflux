package com.config

import com.features.auth.UserSession
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.SessionsConfig
import io.ktor.server.sessions.cookie
import kotlin.text.Charsets

fun SessionsConfig.sessions(
    cookieName: String,
    authSecret: String,
) {
    cookie<UserSession>(cookieName) {
        val secretBytes = authSecret.toByteArray(Charsets.UTF_8)
        transform(SessionTransportTransformerMessageAuthentication(secretBytes))
    }
}
