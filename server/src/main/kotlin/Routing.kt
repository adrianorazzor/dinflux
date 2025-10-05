package com

import com.features.auth.AuthService
import com.features.auth.UserSession
import com.features.auth.authRoutes
import com.features.dashboard.respondDashboard
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val authService by inject<AuthService>()

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause", status = HttpStatusCode.InternalServerError)
        }
    }
    authRoutes(authService)

    routing {
        authenticate("session") {
            get("/") {
                val session = call.sessions.get<UserSession>()
                call.respondDashboard(session)
            }
        }
    }
}
