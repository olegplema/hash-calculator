package com.plema.domain

import kotlinx.coroutines.channels.Channel
import java.io.File
import java.security.MessageDigest
import java.util.*

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

class HashProcess(private val _algorithms: Array<String>, val file: File) {
    private val _hashes = ArrayList<Hash>()
    val channel = Channel<ByteArray>()
    val notificationsChannel = Channel<Long>(Channel.CONFLATED)

    var isDone = false
        private set

    fun finish() {
        isDone = true
    }

    val hashes: List<Hash>
        get() = _hashes

    var isStopped = false
        private set

    fun stopProcess() {
        isStopped = true
    }

    fun initDigests() {
        _algorithms.forEach {
            _hashes.add(Hash(it))
        }
    }
}