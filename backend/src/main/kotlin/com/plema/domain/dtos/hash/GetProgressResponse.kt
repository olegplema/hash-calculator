package com.plema.domain.dtos.hash

import kotlinx.serialization.Serializable

@Serializable
class GetProgressResponse(val bytesRead: Long, val totalBytes: Long, val isStopped: Boolean) {
}