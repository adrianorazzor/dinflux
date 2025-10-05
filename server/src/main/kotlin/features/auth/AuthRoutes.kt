package com.features.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun Application.authRoutes(authService: AuthService) {
    routing {
        route("/auth") {
            post("/register") {
                val params = call.receiveParameters()
                val email = params["email"].orEmpty()
                val displayName = params["displayName"].orEmpty()
                val password = params["password"].orEmpty()

                authService.register(email, displayName, password)
                    .onSuccess {
                        call.sessions.set(it)
                        call.respond(HttpStatusCode.Created)
                    }
                    .onFailure {
                        call.respondText(it.message ?: "registration_failed", status = HttpStatusCode.BadRequest)
                    }
            }

            post("/login") {
                val params = call.receiveParameters()
                val email = params["email"].orEmpty()
                val password = params["password"].orEmpty()

                authService.login(email, password)
                    .onSuccess {
                        call.sessions.set(it)
                        call.respond(HttpStatusCode.OK)
                    }
                    .onFailure {
                        call.respondText(it.message ?: "login_failed", status = HttpStatusCode.Unauthorized)
                    }
            }

            post("/logout") {
                call.sessions.clear<UserSession>()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}
