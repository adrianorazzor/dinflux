package com.features.auth

import io.ktor.resources.Resource

@Resource("/auth")
class AuthResources {
    @Resource("/login")
    data class Login(
        val parent: AuthResources = AuthResources(),
        val error: String? = null,
        val email: String? = null,
    )

    @Resource("/register")
    data class Register(val parent: AuthResources = AuthResources())

    @Resource("/logout")
    data class Logout(val parent: AuthResources = AuthResources())
}
