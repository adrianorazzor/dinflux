package com.di

import com.features.auth.AuthService
import com.features.auth.PasswordHasher
import com.features.auth.UserRepository
import org.koin.dsl.module

val appModule =
    module {
        single { PasswordHasher() }
        single { UserRepository() }
        single { AuthService(get(), get()) }
    }
