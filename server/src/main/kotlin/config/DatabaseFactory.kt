package com.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.jdbc.Database

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        val dbConfig = config.config("postgres")
        val dataSource = hikari(dbConfig)
        Flyway.configure()
            .dataSource(dataSource)
            .locations("classpath:db/migration")
            .load()
            .migrate()
        Database.connect(dataSource)
    }

    private fun hikari(dbConfig: ApplicationConfig): HikariDataSource =
        HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = dbConfig.property("url").getString()
            username = dbConfig.property("user").getString()
            password = dbConfig.property("password").getString()
            maximumPoolSize = 5
            isAutoCommit = false
            validate()
        }.let(::HikariDataSource)
}
