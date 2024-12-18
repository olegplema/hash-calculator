package com.plema.domain.dtos.hash

import kotlinx.serialization.Serializable

@Serializable
class StopProgressRequest(val processId: String) {
}