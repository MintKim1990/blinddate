package com.mint.blinddate.service.cam

import com.fasterxml.jackson.databind.ObjectMapper
import com.mint.blinddate.domain.cam.CamRoom
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class CamService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    private val logger = KotlinLogging.logger {}
    private val camRooms : ConcurrentHashMap<String, CamRoom> = ConcurrentHashMap()

    companion object {
        const val ROOM_KEY = "CamRoom"
    }

    suspend fun createRoom(name: String, owner: String): CamRoom {
        val ops = redisTemplate.opsForHash<String, String>()
        return CamRoom(name, owner).also {
            ops.putIfAbsent(ROOM_KEY, it.id, objectMapper.writeValueAsString(it)).awaitSingle()
        }
    }

}