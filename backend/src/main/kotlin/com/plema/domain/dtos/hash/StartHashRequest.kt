package com.plema.domain.dtos.hash

import kotlinx.serialization.Serializable

@Serializable
class StartHashRequest(val path: String, val algorithms: Array<String>)