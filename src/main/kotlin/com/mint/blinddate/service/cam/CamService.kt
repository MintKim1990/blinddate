package com.mint.blinddate.service.cam

import com.fasterxml.jackson.databind.ObjectMapper
import com.mint.blinddate.domain.cam.CamMessage
import com.mint.blinddate.domain.cam.CamMessageCommand.*
import com.mint.blinddate.domain.cam.CamRoom
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Service
class CamService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {

    private val logger = KotlinLogging.logger {}
    private val camRooms : ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap()

    companion object {
        const val ROOM_KEY = "CamRoom"
    }

    suspend fun createRoom(name: String, owner: String): CamRoom {
        val ops = redisTemplate.opsForHash<String, String>()
        return CamRoom(name, owner).also {
            ops.putIfAbsent(ROOM_KEY, it.id, objectMapper.writeValueAsString(it)).awaitSingle()
        }
    }

    suspend fun handleMessage(session: WebSocketSession, message: WebSocketMessage, camRoomId: String): WebSocketMessage {
        val camMessage = objectMapper.readValue(message.payloadAsText, CamMessage::class.java)
        val ops = redisTemplate.opsForHash<String, String>()
        return when(camMessage.command) {
            JOIN -> {
                ops.get(ROOM_KEY, camRoomId).awaitSingle()?.let {
                    val camRoom = objectMapper.readValue(it, CamRoom::class.java)
                    camRoom.join(camMessage.from)
                    camRooms[camRoomId] = session
                    session.textMessage(judgeNegotiate())
                } ?: throw IllegalArgumentException("종료된 방입니다.")
            }
            OFFER, ANSWER, ICE -> {
                camMessage.candidate = camMessage.candidate?.let {
                    it.substring(0, 64)
                } ?: camMessage.sdp!!.substring(0, 64)

                camRooms[camRoomId]?.let {
                    it.send(Mono.just(it.textMessage(objectMapper.writeValueAsString(camMessage))))
                }

                message
            }
            TEXT -> message
            LEAVE -> message
            else -> throw IllegalArgumentException("지원하지 않는 기능입니다.")
        }
    }

    // ICE 협상조건 판단 (방에 2명 이상에 회원이 들어올경우 수행)
    private fun judgeNegotiate() = if (camRooms.size == 1) "wait" else "negotiate"

}