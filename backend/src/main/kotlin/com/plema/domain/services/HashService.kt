package com.plema.domain.services

import com.plema.domain.Hash
import com.plema.domain.HashProcess
import com.plema.domain.dtos.hash.GetProgressResponse
import com.plema.domain.dtos.hash.HashResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.callbackFlow
import java.util.ArrayList

class HashService {
    private val fileService = FileService()

    suspend fun startHash(process: HashProcess) {
        withContext(Dispatchers.IO) {
            launch {
//                fileService.readFile(process, file)
            }

//            calculateHashes(process.filesHashes, process.channel)
//            finalizeHashes(process.filesHashes)

            process.finish()
            process.notificationsChannel.close()
        }
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

    suspend fun waitResult(process: HashProcess): List<HashResult> {
//        if (process.isDone) {
//            return process.filesHashes.map {
//                HashResult(it.messageDigest.algorithm, it.hexString)
//            }
//        }
//
//        var result: List<HashResult>? = null
//        callbackFlow {
//            process.notificationsChannel.invokeOnClose {
//                if (process.isStopped) {
//                    trySend(ArrayList(0))
//                } else {
//                    trySend(process.filesHashes.map {
//                        HashResult(it.messageDigest.algorithm, it.hexString)
//                    })
//                }
//                close()
//            }
//            awaitClose()
//        }.collect { hashResults ->
//            result = hashResults
//        }
//
//        return result!!
        return ArrayList()
    }

    suspend fun getProgress(process: HashProcess): GetProgressResponse {
        println(process.notificationsChannel.isClosedForSend.toString() + " " + process.isStopped.toString())
        if (process.notificationsChannel.isClosedForSend || process.isStopped) {
            return GetProgressResponse(process.dir.length(), process.dir.length(), true)
        }

        val bytesRead = try {
            process.notificationsChannel.receive()
        } catch (e: ClosedReceiveChannelException) {
            process.dir.length()
        }

        return GetProgressResponse(bytesRead, process.dir.length(), process.isStopped)
    }
}