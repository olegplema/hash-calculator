package com.plema.domain.dtos.hash

import kotlinx.serialization.Serializable

@Serializable
data class StartHashResponse(val message: String, val processId: String)
{
}