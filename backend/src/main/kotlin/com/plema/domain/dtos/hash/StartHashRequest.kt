package com.plema.domain.dtos.hash

enum class HashAlgorithms(val algorithmName: String) {
	MD5("MD-5"), SHA1("SHA-1"), SHA256("SHA-256")
}

data class StartHashRequest(val path: String, val algorithms: Array<HashAlgorithms>) {
}