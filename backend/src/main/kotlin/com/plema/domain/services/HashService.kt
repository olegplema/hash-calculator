package com.plema.domain.services

import com.plema.domain.Hash
import com.plema.domain.HashProcess
import com.plema.domain.dtos.hash.GetProgressResponse
import com.plema.domain.dtos.hash.ProcessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HashService
{
	private val fileService = FileService()

	suspend fun startHash(process: HashProcess)
	{
		withContext(Dispatchers.IO) {
			val jobs = process.filesHashes.map { (key, value) ->
				launch {
					calculateFileHashes(key, value, process.isStopped, process.notificationsChannel)
				}
			}

			jobs.joinAll()
			process.finish()
			process.notificationsChannel.close()
		}
	}

	private suspend fun calculateFileHashes(
		file: File,
		hashes: List<Hash>,
		isStopped: Boolean,
		notificationChannel: SendChannel<Long>
	                                       ) =
		withContext(Dispatchers.IO) {
			val channel = Channel<ByteArray>()
			launch {
				fileService.readFile(file, channel, isStopped)
			}
			calculateHashes(hashes, channel)
			finalizeHashes(hashes)

			notificationChannel.send(file.length())
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

	suspend fun waitResult(process: HashProcess): ProcessResult
	{
		val result = ProcessResult()
		if (process.isDone)
		{
			result.convertToResult(process.filesHashes)
			return result
		}

		callbackFlow {
			process.notificationsChannel.invokeOnClose {
				if (process.isStopped)
				{
					trySend(ArrayList(0))
				} else
				{
					trySend(process.filesHashes.map {

					})
				}
				close()
			}
			awaitClose()
		}.collect { hashResults ->
			result.convertToResult(process.filesHashes)
		}

		return result
	}

	suspend fun getProgress(process: HashProcess): GetProgressResponse
	{
		println(process.notificationsChannel.isClosedForSend.toString() + " " + process.isStopped.toString())
		if (process.notificationsChannel.isClosedForSend || process.isStopped)
		{
			return GetProgressResponse(process.dir.length(), process.dir.length(), true)
		}

		val bytesRead = try
		{
			val fileSize = process.notificationsChannel.receive()
			process.addReadBytes(fileSize)
			process.totalBytesRead
		} catch (_: ClosedReceiveChannelException)
		{
			process.dir.length()
		}

		return GetProgressResponse(bytesRead, process.dir.length(), process.isStopped)
	}
}