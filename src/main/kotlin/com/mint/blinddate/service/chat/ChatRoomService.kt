package com.mint.blinddate.service.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.mint.blinddate.domain.chat.ChatRoom
import com.mint.blinddate.domain.chat.ChatRoomMessageStream
import com.mint.blinddate.service.ChatRequest
import com.mint.blinddate.service.MessageCommand
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import mu.KotlinLogging
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatRoomService(
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
    private val reactiveRedisMessageListenerContainer: ReactiveRedisMessageListenerContainer
) {

    private val logger = KotlinLogging.logger {}
    private val roomMap : ConcurrentHashMap<String, ChatRoomMessageStream> = ConcurrentHashMap()

    companion object {
        const val ROOM_KEY = "ChatRoom"
    }

    suspend fun createRoom(name: String): ChatRoom {
        val ops = redisTemplate.opsForHash<String, String>()
        return ChatRoom(name).also {
            ops.putIfAbsent(ROOM_KEY, it.id, objectMapper.writeValueAsString(it)).awaitSingle()
            roomMap.putIfAbsent(it.id, ChatRoomMessageStream(it.id, reactiveRedisMessageListenerContainer))
        }
    }

    suspend fun findRooms(): List<ChatRoom> {
        val ops = redisTemplate.opsForHash<String, String>()
        return ops.values(ROOM_KEY)
            .map { objectMapper.readValue(it, ChatRoom::class.java) }
            .asFlow()
            .toList()
    }

    fun subscribe(session: WebSocketSession, chatRoomId: String): Flux<WebSocketMessage> {
        return roomMap[chatRoomId]?.let { stream ->
            stream.asStream()
                .doOnCancel { clearRoom(stream, chatRoomId) }
                .map { session.textMessage(it) }
        } ?: throw IllegalArgumentException("종료된 방입니다.")
    }

    private fun clearRoom(stream: ChatRoomMessageStream, chatRoomId: String) {
        if (stream.isChatRoomEmpty()) {
            stream.dispose()
            roomMap.remove(chatRoomId)
            // TODO 분산환경일때를 기준으로 레디스 채팅방 인원 수 체크하여 방 제거 체크로직 필요
            redisTemplate.opsForHash<String, String>().remove(ROOM_KEY, chatRoomId).subscribe()
        }
    }

    fun publish(message: WebSocketMessage, chatRoomId: String) : Mono<Long> {
        val chatRequest = objectMapper.readValue(message.payloadAsText, ChatRequest::class.java)
        when(chatRequest.command) {
            MessageCommand.JOIN -> chatRequest.message = chatRequest.sender + "님이 입장하셨습니다."
            MessageCommand.TALK -> chatRequest.message = chatRequest.message
            else -> throw IllegalArgumentException("지원하지 않는 기능입니다.")
        }
        return redisTemplate.convertAndSend(chatRoomId, objectMapper.writeValueAsString(chatRequest))
    }

}