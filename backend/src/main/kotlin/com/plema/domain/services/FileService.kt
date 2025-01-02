package com.plema.domain.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.withContext
import java.io.FileInputStream
import java.io.File

class FileService {
    suspend fun readFile(file: File, channel: SendChannel<ByteArray>, isStopped: Boolean) =
        withContext(Dispatchers.IO) {
            var bytesRead: Int
            val buffer = ByteArray(2097152)
            val fileInputStream = FileInputStream(file)

            try {
                while (fileInputStream.read(buffer).also { bytesRead = it } > 0 && !isStopped) {
                    channel.send(buffer.copyOf(bytesRead))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fileInputStream.close()
                channel.close()
            }
        }
}