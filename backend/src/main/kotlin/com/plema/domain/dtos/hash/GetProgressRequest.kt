package com.plema.domain.dtos.hash

import kotlinx.serialization.Serializable

@Serializable
class GetProgressRequest(val processId: String) {
}