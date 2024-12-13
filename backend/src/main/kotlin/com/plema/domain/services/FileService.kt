package com.plema.domain.services

import com.plema.domain.dtos.file.ReadResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileInputStream
import java.util.*

class FileService
{

	fun getAllFiles(directoryPath: String): List<File>
	{
		val directory = File(directoryPath)
		if (!directory.exists() || !directory.isDirectory)
		{
			throw IllegalArgumentException("Invalid directory path")
		}

		return directory.walk().filter { it.isFile }.toList()
	}

	fun readFile(file: File, processId: UUID) = flow<ReadResult> {
		val fileInputStream = FileInputStream(file)
		var bytesRead: Int
		val buffer = ByteArray(4096)

		try
		{
			while (fileInputStream.read(buffer).also { bytesRead = it } > 0)
			{
				val readResult = ReadResult(buffer.copyOf(bytesRead), processId, file.absolutePath)
				emit(readResult)
			}
			val lastResult = ReadResult(ByteArray(0), processId, file.absolutePath)
			emit(lastResult)
		} catch (e: Error)
		{
			e.printStackTrace()
		} finally
		{
			fileInputStream.close()
		}
	}.flowOn(Dispatchers.IO)
}