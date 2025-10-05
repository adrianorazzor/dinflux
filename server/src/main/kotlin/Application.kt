package com

import com.config.DatabaseFactory
import com.config.sessions
import com.features.auth.AuthService
import com.features.auth.PasswordHasher
import com.features.auth.UserRepository
import com.features.auth.UserSession
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.Sessions

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init(environment.config)

    val userRepository = UserRepository()
    val passwordHasher = PasswordHasher()
    val authService = AuthService(userRepository, passwordHasher)
    val authSecret = environment.config.property("auth.secret").getString()
    val cookieName = environment.config.property("auth.cookie.name").getString()

    install(Sessions) {
        sessions(cookieName = cookieName, authSecret = authSecret)
    }

    install(Authentication) {
        session<UserSession>("session") {
            validate { session ->
                authService.getSession(session.userId)
            }
            challenge {
                call.respondRedirect("/auth/login")
            }
        }
    }
    configureRouting(authService)
}
