package com.plema.domain.dtos.hash

import kotlinx.serialization.Serializable

@Serializable
class HashResult(val algorithm: String, val hash: String?)