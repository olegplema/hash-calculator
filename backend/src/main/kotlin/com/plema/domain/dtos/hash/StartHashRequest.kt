package com.plema.domain.dtos.hash

import kotlinx.serialization.Serializable

enum class HashAlgorithms(val algorithmName: String)
{
	MD5("MD5"), SHA1("SHA-1"), SHA256("SHA-256")
}

@Serializable
data class StartHashRequest(val path: String, val algorithms: Array<HashAlgorithms>)
{
}