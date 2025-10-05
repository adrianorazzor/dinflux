package com.features.auth

import com.features.shared.isHtmxRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.get
import io.ktor.server.resources.href
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.sessions.clear
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set

fun Application.authRoutes(authService: AuthService) {
    routing {
        get<AuthResources.Login> { resource ->
            if (call.sessions.get<UserSession>() != null) {
                call.respondRedirect("/")
                return@get
            }

            val email = resource.email.orEmpty()
            val error = resource.error
            call.respondLoginPage(error, email)
        }

        post<AuthResources.Register> {
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

        post<AuthResources.Login> {
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
                        call.respondLoginCard(message, email)
                    } else {
                        val loginUrl = call.application.href(AuthResources.Login(error = message, email = email))
                        call.respondRedirect(loginUrl)
                    }
                }
        }

        post<AuthResources.Logout> {
            val isHtmx = call.isHtmxRequest()
            call.sessions.clear<UserSession>()
            val loginUrl = call.application.href(AuthResources.Login())
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
