package com.features.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserSession(val userId: String, val email: String)
