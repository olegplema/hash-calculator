package com.plema.domain.dtos.file

import java.util.*

class ReadResult(val byteArray: ByteArray, val processId: UUID, val filePath: String)
{
}