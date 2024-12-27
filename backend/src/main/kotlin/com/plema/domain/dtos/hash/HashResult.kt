package com.plema.domain.dtos.hash

import com.plema.domain.Hash
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import java.io.File
import java.util.concurrent.ConcurrentHashMap

@Serializable
class HashResult(val algorithm: String, val hash: String?)

object MapSerializer : KSerializer<Map<String, List<HashResult>>>
{
	override val descriptor: SerialDescriptor =
		MapSerializer(String.serializer(), ListSerializer(HashResult.serializer())).descriptor

	override fun serialize(
		encoder: kotlinx.serialization.encoding.Encoder, value: Map<String, List<HashResult>>
	                      )
	{
		encoder.encodeSerializableValue(
			MapSerializer(String.serializer(), ListSerializer(HashResult.serializer())), value.toMap()
		                               )
	}

	override fun deserialize(decoder: Decoder): ConcurrentHashMap<String, List<HashResult>>
	{
		val map = decoder.decodeSerializableValue(
			MapSerializer(String.serializer(), ListSerializer(HashResult.serializer()))
		                                         )
		return ConcurrentHashMap(map)
	}
}

@Serializable
class ProcessResult
{
	@Serializable(MapSerializer::class)
	lateinit var hashes: Map<String, List<HashResult>>
		private set

	fun convertToResult(map: Map<File, List<Hash>>)
	{
		hashes = map.mapKeys { it.key.absolutePath }.mapValues { (_, hashList) ->
				hashList.map { hash ->
					HashResult(
						algorithm = hash.messageDigest.algorithm, hash = hash.hexString
					          )
				}
			}
	}
}