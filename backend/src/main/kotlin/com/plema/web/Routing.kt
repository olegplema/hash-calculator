package com.plema.web

import com.plema.web.controllers.HashController
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val hashController = HashController()

    routing {
        route("hash") {
            post("start") { hashController.startHashing(call) }
            get("progress") { hashController.getProgress(call) }
            post("stop") { hashController.stopProcess(call) }
            get("result") { hashController.getResult(call) }
        }
    }
}
