package com.plema.domain.services

import com.plema.domain.HashProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileInputStream

class FileService {
    suspend fun readFile(process: HashProcess) =
        withContext(Dispatchers.IO) {
            var bytesRead: Int
            val buffer = ByteArray(2097152)
            val fileInputStream = FileInputStream(process.file)
            try {
                while (fileInputStream.read(buffer).also { bytesRead = it } > 0 && !process.isStopped) {
                    process.notificationsChannel.send(fileInputStream.available())
                    process.channel.send(buffer.copyOf(bytesRead))
                }
            } catch (e: Error) {
                e.printStackTrace()
            } finally {
                fileInputStream.close()
                process.channel.close()
            }
        }
}