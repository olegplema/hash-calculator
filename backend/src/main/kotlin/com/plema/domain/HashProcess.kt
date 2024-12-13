package com.plema.domain

import com.plema.domain.dtos.hash.HashAlgorithms
import io.ktor.util.collections.*
import kotlinx.coroutines.Job
import java.io.File
import java.security.MessageDigest
import java.util.*


val hashProcesses = ConcurrentMap<UUID, HashProcess>()

class Hash(var messageDigest: MessageDigest)
{
	private var hexString: String? = null

	fun getHexString() = hexString

	fun calculateHexString()
	{
		val res = messageDigest.digest()
		hexString = res.joinToString("") { "%02x".format(it) }
	}
}

class HashProcess(private val _algorithms: Array<HashAlgorithms>)
{
	private val _files = ConcurrentMap<String, List<Hash>>()

	val files: Map<String, List<Hash>>
		get() = _files

	val algorithms: Array<HashAlgorithms>
		get() = _algorithms

	private var _job: Job? = null
		set(value)
		{
			if (field == null)
			{
				field = value
			}
		}

	fun initFiles(dirFiles: List<File>)
	{
		dirFiles.forEach {
			val hashes = ArrayList<Hash>()
			algorithms.forEach {
				val md = MessageDigest.getInstance(it.algorithmName)
				hashes.add(Hash(md))
			}

			_files[it.absolutePath] = hashes
		}
	}

	fun getJob() = _job

	fun setJob(value: Job)
	{
		_job = value
	}
}