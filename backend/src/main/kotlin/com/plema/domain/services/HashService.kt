package com.plema.domain.services

import com.plema.domain.Hash
import com.plema.domain.HashProcess
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach

class HashService {
    private val fileService = FileService()

    suspend fun startHash(process: HashProcess) =
        withContext(Dispatchers.IO) {
            launch {
                fileService.readFile(process)
            }

            calculateHashes(process.hashes, process.channel)
            finalizeHashes(process.hashes)

            process.finish()
            process.hashes.forEach {
                println(it.hexString)
            }
            process.notificationsChannel.close()
        }

    private suspend fun calculateHashes(hashes: List<Hash>, receiveChannel: ReceiveChannel<ByteArray>) =
        withContext(Dispatchers.Default) {
            receiveChannel.consumeEach {
                hashes.map { hash ->
                    launch {
                        hash.messageDigest.update(it)
                    }
                }.joinAll()
            }
        }

    private suspend fun finalizeHashes(hashes: List<Hash>) = withContext(Dispatchers.Default) {
        hashes.map {
            launch {
                it.calculateHexString()
            }
        }.joinAll()
    }
}