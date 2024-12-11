package com.plema

import com.plema.web.configureRouting
import io.ktor.server.application.*
import io.ktor.util.collections.ConcurrentMap

val processes = ConcurrentMap<String, String>();

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
