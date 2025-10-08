package com.features.auth

import com.features.shared.isHtmxRequest
import com.features.shared.respondWithRedirect
import io.ktor.server.application.Application
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveParameters
import io.ktor.server.resources.get
import io.ktor.server.resources.href
import io.ktor.server.resources.post
import io.ktor.server.response.respondRedirect
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

        get<AuthResources.Register> {
            if (call.sessions.get<UserSession>() != null) {
                call.respondRedirect("/")
                return@get
            }

            val form = RegisterFormState()
            if (call.isHtmxRequest()) {
                call.respondRegisterCard(form)
            } else {
                call.respondRegisterPage(form)
            }
        }

        post<AuthResources.Register> {
            val params = call.receiveParameters()
            val email = params["email"].orEmpty().trim()
            val displayName = params["displayName"].orEmpty().trim()
            val password = params["password"].orEmpty()
            val confirmPassword = params["confirmPassword"].orEmpty()
            val isHtmx = call.isHtmxRequest()

            val formState =
                RegisterFormState(
                    displayName = displayName,
                    email = email,
                    password = "",
                    confirmPassword = "",
                )

            val validationError = validateRegisterForm(displayName, email, password, confirmPassword)
            if (validationError != null) {
                call.respondRegister(formState.copy(errorMessage = validationError), isHtmx)
                return@post
            }

            authService.register(email, displayName, password)
                .onSuccess {
                    call.sessions.set(it)
                    call.respondWithRedirect("/", isHtmx)
                }
                .onFailure {
                    val message =
                        when (it.message) {
                            "email_in_use" -> "E-mail já cadastrado."
                            else -> it.message ?: "Não foi possível concluir o cadastro."
                        }
                    call.respondRegister(formState.copy(errorMessage = message), isHtmx)
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

private suspend fun ApplicationCall.respondRegister(form: RegisterFormState, isHtmx: Boolean) {
    if (isHtmx) {
        respondRegisterCard(form)
    } else {
        respondRegisterPage(form)
    }
}

private fun validateRegisterForm(
    displayName: String,
    email: String,
    password: String,
    confirmPassword: String,
): String? {
    if (displayName.isBlank()) return "Informe seu nome."
    if (email.isBlank() || !email.contains('@')) return "Informe um e-mail válido."
    if (password.length < 8) return "Use uma senha com pelo menos 8 caracteres."
    if (password != confirmPassword) return "As senhas não coincidem."
    return null
}
