package com.features.dashboard

import com.features.auth.UserSession
import com.features.shared.classNames
import io.ktor.server.application.ApplicationCall
import io.ktor.server.html.respondHtml
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.lang
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr

suspend fun ApplicationCall.respondDashboard(session: UserSession?) {
    respondHtml {
        dashboardPage(session)
    }
}

private fun HTML.dashboardPage(session: UserSession?) {
    lang = "pt-BR"
    head {
        meta { charset = "utf-8" }
        meta {
            name = "viewport"
            content = "width=device-width, initial-scale=1"
        }
        title { +"Dinflux · Dashboard" }
        script(src = "https://cdn.tailwindcss.com") {}
        script(src = "https://unpkg.com/htmx.org@1.9.12") {}
    }
    body(classes = "min-h-screen bg-slate-950 text-slate-100") {
        div(classes = "mx-auto flex min-h-screen w-full max-w-6xl flex-col gap-8 px-6 py-10") {
            topBar(session)
            kpiSection()
            placeholders()
        }
    }
}

private fun FlowContent.topBar(session: UserSession?) {
    div(
        classes =
            classNames(
                "flex",
                "flex-col",
                "gap-4",
                "rounded-2xl",
                "bg-slate-900/60",
                "p-6",
                "ring-1",
                "ring-slate-800",
                "sm:flex-row",
                "sm:items-center",
                "sm:justify-between",
            ),
    ) {
        div {
            h1(classes = "text-2xl font-semibold text-slate-50") { +"Dashboard" }
            p(classes = "text-sm text-slate-400") { +"Visão geral do mês corrente" }
        }
        div(
            classes =
                classNames(
                    "flex",
                    "flex-wrap",
                    "items-center",
                    "gap-3",
                    "text-sm",
                    "text-slate-300",
                ),
        ) {
            session?.let {
                span(
                    classes =
                        classNames(
                            "rounded-full",
                            "bg-slate-800/70",
                            "px-3",
                            "py-1",
                            "text-xs",
                            "uppercase",
                            "tracking-wide",
                            "text-slate-400",
                        ),
                ) { +it.email }
            }
            form {
                action = "/auth/logout"
                method = kotlinx.html.FormMethod.post
                attributes["hx-post"] = "/auth/logout"
                button(
                    type = kotlinx.html.ButtonType.submit,
                    classes =
                        classNames(
                            "rounded-lg",
                            "bg-rose-500",
                            "px-4",
                            "py-2",
                            "text-xs",
                            "font-semibold",
                            "text-rose-950",
                            "transition",
                            "hover:bg-rose-400",
                            "focus:outline-none",
                            "focus:ring-2",
                            "focus:ring-rose-400/60",
                        ),
                ) { +"Sair" }
            }
        }
    }
}

private fun FlowContent.kpiSection() {
    div(classes = "grid gap-4 sm:grid-cols-2 xl:grid-cols-4") {
        kpiCard("Ingressos", "R$ 0,00", "bg-emerald-500/10 text-emerald-300")
        kpiCard("Despesas (cash)", "R$ 0,00", "bg-rose-500/10 text-rose-300")
        kpiCard("Pagamentos cartão", "R$ 0,00", "bg-sky-500/10 text-sky-300")
        kpiCard("Saldo", "R$ 0,00", "bg-slate-800/80 text-slate-200")
    }
}

private fun FlowContent.kpiCard(
    title: String,
    value: String,
    extraClasses: String,
) {
    div(classes = "rounded-2xl bg-slate-900/60 p-5 ring-1 ring-slate-800") {
        span(classes = "text-xs uppercase tracking-wide text-slate-500") { +title }
        p(classes = "mt-3 text-2xl font-semibold $extraClasses") { +value }
    }
}

private fun FlowContent.placeholders() {
    div(classes = "grid gap-6 lg:grid-cols-3") {
        div(classes = "rounded-2xl bg-slate-900/60 p-6 ring-1 ring-slate-800 lg:col-span-2") {
            h1(classes = "text-lg font-semibold text-slate-100") { +"Calendário de Caixa" }
            p(classes = "mt-2 text-sm text-slate-400") { +"Em breve, próximos vencimentos e recorrências aparecerão aqui." }
        }
        div(classes = "rounded-2xl bg-slate-900/60 p-6 ring-1 ring-slate-800") {
            h1(classes = "text-lg font-semibold text-slate-100") { +"Categorias" }
            table(classes = "mt-4 w-full text-left text-sm text-slate-300") {
                thead {
                    tr {
                        th(classes = "pb-2 font-medium text-slate-500") { +"Categoria" }
                        th(classes = "pb-2 font-medium text-right text-slate-500") { +"Despesas" }
                    }
                }
                tbody {
                    repeat(4) { index ->
                        tr(classes = "border-t border-slate-800/60") {
                            td(classes = "py-2") { +"Categoria ${index + 1}" }
                            td(classes = "py-2 text-right text-slate-400") { +"R$ 0,00" }
                        }
                    }
                }
            }
        }
    }
}
