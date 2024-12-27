package com.plema.domain

import kotlinx.coroutines.channels.Channel
import java.io.File
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap


class Hash(algorithm: String)
{
	var messageDigest: MessageDigest
		private set

	init
	{
		messageDigest = MessageDigest.getInstance(algorithm)
	}

	var hexString: String? = null
		private set

	fun calculateHexString()
	{
		val res = messageDigest.digest()
		hexString = res.joinToString("") { "%02x".format(it) }
	}
}

class HashProcess(private val _algorithms: Array<String>, val dir: File)
{
	private val _filesHashes = ConcurrentHashMap<File, List<Hash>>()
	val notificationsChannel = Channel<Long>(100)
	var totalBytesRead = 0L
		private set

	var isDone = false
		private set

	fun finish()
	{
		isDone = true
	}

	val filesHashes: Map<File, List<Hash>>
		get() = _filesHashes

	var isStopped = false
		private set

	fun stopProcess()
	{
		isStopped = true
	}

	fun addReadBytes(bytesRead: Long) {
		totalBytesRead += bytesRead
	}

	private fun getAllFiles(directory: File): List<File>
	{
		return if (directory.exists() && directory.isDirectory)
		{
			directory.walk().filter { it.isFile }.toList()
		} else
		{
			emptyList()
		}
	}

	fun initDigests()
	{
		val files = getAllFiles(dir)

		files.forEach { file ->
			_filesHashes[file] = _algorithms.map {
				Hash(it)
			}
		}
	}
}