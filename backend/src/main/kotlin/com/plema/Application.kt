package com.plema

import com.plema.domain.dtos.hash.HashAlgorithms
import com.plema.domain.dtos.hash.StartHashRequest
import com.plema.domain.hashProcesses
import com.plema.domain.services.HashService
import com.plema.web.configureRouting
import com.plema.web.controllers.HashController
import io.ktor.server.application.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    //io.ktor.server.netty.EngineMain.main(args)

    val time = measureTimeMillis {

        val hashService = HashService()
        val hashAlgorithms = arrayOf(HashAlgorithms.MD5, HashAlgorithms.SHA1, HashAlgorithms.SHA256)
        val path = "/home/atola/Pictures"
        val startHash = StartHashRequest(path, hashAlgorithms)

        runBlocking {
            launch { hashService.startHash(startHash) }
        }

        for ((_,pValue) in hashProcesses) {
            pValue.files.forEach {
                println(it.key)
                it.value.forEach {
                    println("     ${it.messageDigest.algorithm}: ${it.getHexString()}")
                }
            }
        }
    }
    println("$time ms")
}

fun Application.module()
{
    configureRouting()
}