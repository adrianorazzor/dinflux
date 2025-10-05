package com.features.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun Application.authRoutes(authService: AuthService) {
    routing {
        route("/auth") {
            get("/login") {
                if (call.sessions.get<UserSession>() != null) {
                    call.respondRedirect("/")
                    return@get
                }

                val error = call.request.queryParameters["error"]
                val email = call.request.queryParameters["email"].orEmpty()
                call.respondLoginPage(error, email)
            }

            post("/register") {
                val params = call.receiveParameters()
                val email = params["email"].orEmpty()
                val displayName = params["displayName"].orEmpty()
                val password = params["password"].orEmpty()

                authService.register(email, displayName, password)
                    .onSuccess {
                        call.sessions.set(it)
                        call.respondWithRedirect("/", isHtmx = call.isHtmxRequest())
                    }
                    .onFailure {
                        call.respondText(it.message ?: "registration_failed", status = HttpStatusCode.BadRequest)
                    }
            }

            post("/login") {
                val params = call.receiveParameters()
                val email = params["email"].orEmpty().trim()
                val password = params["password"].orEmpty()
                val isHtmx = call.isHtmxRequest()

                authService.login(email, password)
                    .onSuccess {
                        call.sessions.set(it)
                        call.respondWithRedirect("/", isHtmx)
                    }
                    .onFailure { throwable ->
                        val message = throwable.toLoginErrorMessage()
                        if (isHtmx) {
                            call.respondLoginCard(message, email, HttpStatusCode.Unauthorized)
                        } else {
                            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8)
                            val encodedEmail = URLEncoder.encode(email, StandardCharsets.UTF_8)
                            call.respondRedirect("/auth/login?error=$encodedMessage&email=$encodedEmail")
                        }
                    }
            }

            post("/logout") {
                val isHtmx = call.isHtmxRequest()
                call.sessions.clear<UserSession>()
                call.respondWithRedirect("/auth/login", isHtmx)
            }
        }
    }
}

private fun Throwable.toLoginErrorMessage(): String = when (message) {
    "invalid_credentials" -> "E-mail ou senha inválidos."
    "inactive_user" -> "Esta conta está desativada."
    else -> "Não foi possível entrar. Tente novamente."
}

private fun ApplicationCall.isHtmxRequest(): Boolean = request.headers["HX-Request"] == "true"

private suspend fun ApplicationCall.respondWithRedirect(target: String, isHtmx: Boolean) {
    if (isHtmx) {
        response.headers.append("HX-Redirect", target)
        respond(HttpStatusCode.NoContent)
    } else {
        respondRedirect(target)
    }
}
