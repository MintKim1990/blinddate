package com.mint.blinddate.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.mint.blinddate.service.RoomService
import mu.KotlinLogging
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class WebSocketChatHandler(
    private val objectMapper: ObjectMapper,
    private val messagePublisher: MessagePublisher,
    private val roomService: RoomService,
    private val reactiveRedisMessageListenerContainer: ReactiveRedisMessageListenerContainer,
) : WebSocketHandler {

    private val logger = KotlinLogging.logger {}

    companion object {
        const val CHAT_ROOM_ID = "Chat-Room-Id"
    }

    override fun handle(session: WebSocketSession): Mono<Void> {

        logger.info { "최초접근 sessionId : ${session.id}" }






        val chatRoomId = getChatRoomId(session)

        val sendMessage = session.receive()
            .log("${session.id} Receive")
            .filter { it.type == WebSocketMessage.Type.TEXT }
            .flatMap { roomService.publish(it, chatRoomId) }

        val ping = Flux.interval(Duration.ofSeconds(20))
            .map { session.pingMessage { session.bufferFactory().allocateBuffer() } }

        val receiveMessage = session.send(
            Flux.merge(roomService.subscribe(session, chatRoomId), ping)
                .log("${session.id} Send")
        )

        return Flux.zip(sendMessage, receiveMessage).then()
    }

    private fun getChatRoomId(session: WebSocketSession) : String {

        /***
         * 핸드쉐이크 헤더에서 Room 키값을 토큰에서 꺼내오는 방식으로 변경예정,
         * JS에서는 헤더값 추가가 불가능해 QueryParam 방식으로 임시 사용
         *
         * val chatRoomId = session.handshakeInfo.headers.getOrEmpty(CHAT_ROOM_ID)
                                   .stream()
                                   .findFirst() ?: throw IllegalStateException("채팅방 ID가 없습니다.")
         *
         */

        return session.handshakeInfo.uri.query.let {
            it.replace("chatRoomId=", "")
        }
    }
}


