package com.features.shared

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect

suspend fun ApplicationCall.respondWithRedirect(
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
