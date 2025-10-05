package com

import com.config.DatabaseFactory
import com.config.sessionAuth
import com.config.sessions
import com.di.appModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.resources.Resources
import io.ktor.server.sessions.Sessions
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init(environment.config)

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    val authSecret = environment.config.property("auth.secret").getString()
    val cookieName = environment.config.property("auth.cookie.name").getString()

    install(Sessions) {
        sessions(cookieName = cookieName, authSecret = authSecret)
    }

    install(Resources)

    install(Authentication) {
        sessionAuth(application = this@module)
    }

    configureRouting()
}
