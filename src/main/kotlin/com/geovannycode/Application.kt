package com.geovannycode

import com.geovannycode.config.setupApplicationConfiguration
import com.geovannycode.entities.setupDatabase
import com.geovannycode.plugins.configureRouting
import com.geovannycode.plugins.configureSecurity
import com.geovannycode.plugins.configureSerialization
import io.ktor.server.application.Application

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused")
fun Application.module() {
    val applicationConfiguration = setupApplicationConfiguration()
    setupDatabase(applicationConfiguration)
    configureSecurity()
    configureSerialization()
    configureRouting()
}
