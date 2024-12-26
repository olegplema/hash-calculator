package com.plema.domain

import kotlinx.coroutines.channels.Channel
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

class Hash(algorithm: String) {
    var messageDigest: MessageDigest
        private set

    init {
        messageDigest = MessageDigest.getInstance(algorithm)
    }

    var hexString: String? = null
        private set

    fun calculateHexString() {
        val res = messageDigest.digest()
        hexString = res.joinToString("") { "%02x".format(it) }
    }
}

class ProcessedFile(val hashes: List<Hash>) {
    val channel = Channel<ByteArray>()
}

class HashProcess(private val _algorithms: Array<String>, val dir: File) {
    private val _filesHashes = ConcurrentHashMap<File, ProcessedFile>()
    val channel = Channel<ByteArray>()
    val notificationsChannel = Channel<Long>(Channel.CONFLATED)

    var isDone = false
        private set

    fun finish() {
        isDone = true
    }

    val filesHashes: Map<File, ProcessedFile>
        get() = _filesHashes

    var isStopped = false
        private set

    fun stopProcess() {
        isStopped = true
    }


    private fun getAllFiles(directory: File): List<File> {
        return if (directory.exists() && directory.isDirectory) {
            directory.walk().filter { it.isFile }.toList()
        } else {
            emptyList()
        }
    }

    fun initDigests() {
        val files = getAllFiles(dir)

        files.forEach { file ->
            val hashes = _algorithms.map {
                Hash(it)
            }
            _filesHashes[file] = ProcessedFile(hashes)
        }
    }
}