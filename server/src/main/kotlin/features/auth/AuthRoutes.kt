package com.features.auth

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import io.ktor.server.resources.get
import io.ktor.server.resources.href
import io.ktor.server.resources.post

fun Application.authRoutes(authService: AuthService) {
    routing {
        get<AuthLogin> { resource ->
            if (call.sessions.get<UserSession>() != null) {
                call.respondRedirect("/")
                return@get
            }

            val email = resource.email.orEmpty()
            val error = resource.error
            call.respondLoginPage(error, email)
        }

        post<AuthRegister> {
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

        post<AuthLogin> {
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
                        val loginUrl = call.application.href(AuthLogin(error = message, email = email))
                        call.respondRedirect(loginUrl)
                    }
                }
        }

        post<AuthLogout> {
            val isHtmx = call.isHtmxRequest()
            call.sessions.clear<UserSession>()
            val loginUrl = call.application.href(AuthLogin())
            call.respondWithRedirect(loginUrl, isHtmx)
        }
    }
}

private fun Throwable.toLoginErrorMessage(): String =
    when (message) {
        "invalid_credentials" -> "E-mail ou senha inválidos."
        "inactive_user" -> "Esta conta está desativada."
        else -> "Não foi possível entrar. Tente novamente."
    }

private fun ApplicationCall.isHtmxRequest(): Boolean = request.headers["HX-Request"] == "true"

private suspend fun ApplicationCall.respondWithRedirect(
    target: String,
    isHtmx: Boolean,
) {
    if (isHtmx) {
        response.headers.append("HX-Redirect", target)
        respond(HttpStatusCode.NoContent)
    } else {
        respondRedirect(target)
    }
}
