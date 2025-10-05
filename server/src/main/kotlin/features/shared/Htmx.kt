package com.features.shared

import io.ktor.server.application.ApplicationCall

private const val HX_REQUEST_HEADER = "HX-Request"

fun ApplicationCall.isHtmxRequest(): Boolean = request.headers[HX_REQUEST_HEADER]?.equals("true", ignoreCase = true) == true
