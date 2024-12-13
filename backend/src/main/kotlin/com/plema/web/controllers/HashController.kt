package com.plema.web.controllers

import com.plema.domain.dtos.hash.StartHashRequest
import com.plema.domain.services.HashService
import io.ktor.server.request.*
import io.ktor.server.routing.*

class HashController
{
	private val hashService = HashService()

	suspend fun startHashing(call: RoutingCall)
	{
		val startHashData = call.receive<StartHashRequest>()

		hashService.startHash(startHashData)
	}
}
