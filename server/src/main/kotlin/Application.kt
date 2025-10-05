package com

import com.config.DatabaseFactory
import com.features.auth.AuthService
import com.features.auth.PasswordHasher
import com.features.auth.UserRepository
import com.features.auth.UserSession
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.session
import io.ktor.server.response.respondRedirect
import io.ktor.server.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import kotlin.text.Charsets

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
        cookie<UserSession>(cookieName) {
            val secretBytes = authSecret.toByteArray(Charsets.UTF_8)
            transform(SessionTransportTransformerMessageAuthentication(secretBytes))
        }
    }

    install(Authentication) {
        session<UserSession>("session") {
            validate { session ->
                authService.getSession(session.userId)
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }
    configureRouting(authService)
}
