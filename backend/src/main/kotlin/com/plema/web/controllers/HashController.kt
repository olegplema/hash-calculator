package com.plema.web.controllers

import com.plema.domain.HashProcess
import com.plema.domain.dtos.hash.*
import com.plema.domain.services.HashService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.collections.ConcurrentMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.system.measureTimeMillis

class HashController {
    private val hashService = HashService()
    private val hashScope = CoroutineScope(Dispatchers.IO)
    val hashProcesses = ConcurrentMap<UUID, HashProcess>()

    suspend fun startHashing(call: RoutingCall) {
		val startHashData = call.receive<StartHashRequest>()

        val file = File(startHashData.path)
        val process = HashProcess(startHashData.algorithms, file)

        val uuid = UUID.randomUUID()
        process.initDigests()
        hashProcesses[uuid] = process

        hashScope.launch {
            hashService.startHash(process)
        }

        call.respond(StartHashResponse(uuid.toString()))
    }

    suspend fun getProgress(call: RoutingCall) {
        val processId = call.request.queryParameters["processId"]
        val process = hashProcesses[UUID.fromString(processId)] ?: return call.respond(HttpStatusCode.NotFound)

        val progress = hashService.getProgress(process)

        call.respond(progress)
    }

    suspend fun getResult(call: RoutingCall) {
        val processId = call.request.queryParameters["processId"]
        val process = hashProcesses[UUID.fromString(processId)] ?: return call.respond(HttpStatusCode.NotFound)

        val result = hashService.waitResult(process)

        call.respond(result)
    }

    suspend fun stopProcess(call: RoutingCall) {
        val getProgressData = call.receive<StopProgressRequest>()
        val process = hashProcesses[UUID.fromString(getProgressData.processId)] ?: return call.respond(HttpStatusCode.NotFound)
        process.stopProcess()
        call.respond(HttpStatusCode.OK)
    }
}
