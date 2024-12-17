package com.plema.web.controllers

import com.plema.domain.HashProcess
import com.plema.domain.dtos.hash.*
import com.plema.domain.hashProcesses
import com.plema.domain.services.HashService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class HashController {
    private val hashService = HashService()
    private val hashScope = CoroutineScope(Dispatchers.IO)

    suspend fun startHashing(call: RoutingCall) {
		val startHashData = call.receive<StartHashRequest>()

//        val hashAlgorithms = arrayOf("MD5", "SHA-1", "SHA-256")
//        val path = "/Users/oleg/Desktop/largefile.dat"
//        val startHash = StartHashRequest(path, hashAlgorithms)

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
        val getProgressData = call.receive<GetProgressRequest>()
        val process = hashProcesses[UUID.fromString(getProgressData.processId)] ?: return call.respond(HttpStatusCode.NotFound)

        val bytesAvailable = process.notificationsChannel.receive()

        val bytesRead = process.file.length() - bytesAvailable
        call.respond(GetProgressResponse(bytesRead, process.file.length(), process.isStopped))
    }

    suspend fun getResult(call: RoutingCall) {
        val processId = call.request.queryParameters["processId"]
        val process = hashProcesses[UUID.fromString(processId)] ?: return call.respond(HttpStatusCode.NotFound)

        if (process.isDone) {
            val result = process.hashes.map {
                HashResult(it.messageDigest.algorithm, it.hexString ?:   "foo")
            }
            call.respond(result)
            return
        }

        callbackFlow {
            process.notificationsChannel.invokeOnClose {
                trySend(process.hashes.map {
                    HashResult(it.messageDigest.algorithm, it.hexString ?: "foo1")
                })
                close()
            }
            awaitClose()
        }.collect { hashResults ->
            call.respond(hashResults)
        }
    }

    suspend fun stopProcess(call: RoutingCall) {
        val getProgressData = call.receive<GetProgressRequest>()
        val process = hashProcesses[UUID.fromString(getProgressData.processId)] ?: return call.respond(HttpStatusCode.NotFound)
        process.stopProcess()
        call.respond(HttpStatusCode.OK)
    }
}
