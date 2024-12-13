package com.plema.domain.services

import com.plema.domain.HashProcess
import com.plema.domain.dtos.file.ReadResult
import com.plema.domain.dtos.hash.StartHashRequest
import com.plema.domain.hashProcesses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class HashService
{

	private val fileService = FileService()

	suspend fun startHash(startHashData: StartHashRequest): UUID
	{
		val uuid = UUID.randomUUID()
		withContext(Dispatchers.IO) {
			val process = HashProcess(startHashData.algorithms)
			val files = fileService.getAllFiles(startHashData.path)

			process.initFiles(files)
			hashProcesses[uuid] = process

			for (file in files)
			{
				launch {

					fileService.readFile(file, uuid).collect { calculateHashes(it) }
				}

			}
		}

		return uuid
	}

	private suspend fun calculateHashes(readResult: ReadResult) = withContext(Dispatchers.Default) {
		val process = hashProcesses[readResult.processId]
		val algorithms = process?.algorithms
		val currentFile = process?.files[readResult.filePath]

		if (algorithms == null || currentFile == null) return@withContext

		for (hash in currentFile)
		{
			launch {
				if (readResult.byteArray.isEmpty())
				{
					hash.calculateHexString()
				} else
				{
					hash.messageDigest.update(readResult.byteArray)
				}
			}
		}
	}

}