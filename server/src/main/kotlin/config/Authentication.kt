package com.config

import com.features.auth.AuthService
import com.features.auth.UserSession
import io.ktor.server.application.Application
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.session
import io.ktor.server.response.respondRedirect
import org.koin.ktor.ext.inject

fun AuthenticationConfig.sessionAuth(
    application: Application,
    loginPath: String = "/auth/login",
) {
    val authService by application.inject<AuthService>()
    session<UserSession>("session") {
        validate { session -> authService.getSession(session.userId) }
        challenge { call.respondRedirect(loginPath) }
    }
}
