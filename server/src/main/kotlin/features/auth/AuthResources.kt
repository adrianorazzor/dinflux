package com.features.auth

import io.ktor.resources.Resource

@Resource("/auth/login")
data class AuthLogin(val error: String? = null, val email: String? = null)

@Resource("/auth/register")
class AuthRegister

@Resource("/auth/logout")
class AuthLogout
