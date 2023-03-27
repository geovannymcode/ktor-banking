package com.geovannycode

import com.geovannycode.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureSecurity()
    configureSerialization()
    configureRouting()
}
