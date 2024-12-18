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
            var totalBytesRead: Long = 0
            val onePercentSize = process.file.length() / 100 * 5
            var nextNotificationThreshold = onePercentSize

            try {
                while (fileInputStream.read(buffer).also { bytesRead = it } > 0 && !process.isStopped) {
                    totalBytesRead += bytesRead
                    if (totalBytesRead >= nextNotificationThreshold) {
                        process.notificationsChannel.send(totalBytesRead)
                        nextNotificationThreshold += onePercentSize
                    }
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