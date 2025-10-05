package com.features.auth

import com.features.shared.classNames
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondText
import kotlinx.html.FlowContent
import kotlinx.html.FormEncType
import kotlinx.html.FormMethod
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.lang
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.title

suspend fun ApplicationCall.respondLoginPage(
    error: String?,
    email: String,
) {
    respondHtml {
        loginPage(error, email)
    }
}

suspend fun ApplicationCall.respondLoginCard(
    error: String?,
    email: String,
    status: HttpStatusCode,
) {
    val snippet = createHTML().div { loginCard(error, email) }
    respondText(snippet, ContentType.Text.Html, status)
}

private fun HTML.loginPage(
    error: String?,
    email: String,
) {
    lang = "pt-BR"
    head {
        meta { charset = "utf-8" }
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
        }
        title { +"Dinflux · Entrar" }
        script(src = "https://cdn.tailwindcss.com") {}
        script(src = "https://unpkg.com/htmx.org@1.9.12") {}
    }
    body(
        classes =
            classNames(
                "min-h-screen",
                "bg-slate-950",
                "text-slate-100",
                "flex",
                "items-center",
                "justify-center",
                "p-6",
            ),
    ) {
        div {
            loginCard(error, email)
        }
    }
}

private fun FlowContent.loginCard(
    error: String?,
    email: String,
) {
    div(
        classes =
            classNames(
                "w-full",
                "max-w-md",
                "rounded-2xl",
                "bg-slate-900/80",
                "backdrop-blur",
                "shadow-xl",
                "ring-1",
                "ring-slate-800",
                "p-8",
            ),
    ) {
        id = "login-card"
        h1(
            classes =
                classNames(
                    "text-2xl",
                    "font-semibold",
                    "text-slate-50",
                    "mb-6",
                    "text-center",
                ),
        ) {
            +"Entre na sua conta"
        }

        if (!error.isNullOrBlank()) {
            div(
                classes =
                    classNames(
                        "mb-4",
                        "rounded-lg",
                        "border",
                        "border-red-500/40",
                        "bg-red-500/10",
                        "p-4",
                        "text-sm",
                        "text-red-200",
                    ),
            ) {
                span { +error }
            }
        }

        form {
            method = FormMethod.post
            encType = FormEncType.applicationXWwwFormUrlEncoded
            action = "/auth/login"
            attributes["hx-post"] = "/auth/login"
            attributes["hx-target"] = "#login-card"
            attributes["hx-swap"] = "outerHTML"
            classes = setOf("space-y-5")

            div {
                label(
                    classes =
                        classNames(
                            "block",
                            "text-sm",
                            "font-medium",
                            "text-slate-300",
                            "mb-1",
                        ),
                ) {
                    htmlFor = "email"
                    +"E-mail"
                }
                input(
                    type = InputType.email,
                    classes =
                        classNames(
                            "w-full",
                            "rounded-lg",
                            "border",
                            "border-slate-800",
                            "bg-slate-950/60",
                            "px-4",
                            "py-2.5",
                            "text-sm",
                            "text-slate-100",
                            "placeholder:text-slate-500",
                            "focus:border-emerald-500",
                            "focus:outline-none",
                            "focus:ring-2",
                            "focus:ring-emerald-500/60",
                        ),
                ) {
                    id = "email"
                    name = "email"
                    value = email
                    required = true
                    attributes["autocomplete"] = "email"
                }
            }

            div {
                label(
                    classes =
                        classNames(
                            "block",
                            "text-sm",
                            "font-medium",
                            "text-slate-300",
                            "mb-1",
                        ),
                ) {
                    htmlFor = "password"
                    +"Senha"
                }
                input(
                    type = InputType.password,
                    classes =
                        classNames(
                            "w-full",
                            "rounded-lg",
                            "border",
                            "border-slate-800",
                            "bg-slate-950/60",
                            "px-4",
                            "py-2.5",
                            "text-sm",
                            "text-slate-100",
                            "placeholder:text-slate-500",
                            "focus:border-emerald-500",
                            "focus:outline-none",
                            "focus:ring-2",
                            "focus:ring-emerald-500/60",
                        ),
                ) {
                    id = "password"
                    name = "password"
                    required = true
                    attributes["autocomplete"] = "current-password"
                }
            }

            button(
                type = kotlinx.html.ButtonType.submit,
                classes =
                    classNames(
                        "w-full",
                        "rounded-lg",
                        "bg-emerald-500",
                        "px-4",
                        "py-2.5",
                        "text-sm",
                        "font-semibold",
                        "text-emerald-950",
                        "transition",
                        "hover:bg-emerald-400",
                        "focus:outline-none",
                        "focus:ring-2",
                        "focus:ring-emerald-400/60",
                    ),
            ) {
                +"Entrar"
            }
        }

        p(classes = classNames("mt-6", "text-center", "text-xs", "text-slate-500")) {
            +"Não tem uma conta ainda? "
            span(classes = classNames("text-emerald-400")) { +"Registro em breve." }
        }
    }
}
