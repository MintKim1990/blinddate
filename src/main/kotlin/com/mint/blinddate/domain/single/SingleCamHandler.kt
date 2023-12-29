package com.mint.blinddate.domain.single

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class SingleCamHandler(
    private val objectMapper: ObjectMapper,
) : WebSocketHandler {

    private val logger = KotlinLogging.logger {}

    override fun handle(session: WebSocketSession): Mono<Void> {
        logger.info { "message : $session" }
        val message = session.receive()
            .map {
                objectMapper.readValue(it.payloadAsText, SingleCamMessage::class.java)
            }
            .log()

        return message.then()
    }

    fun messageHandle(message: SingleCamMessage): String {
        return when(message.type) {
            SingleCamMessageCommand.JOIN -> ""
            SingleCamMessageCommand.OFFER -> ""
            SingleCamMessageCommand.ANSWER -> ""
            SingleCamMessageCommand.TEXT -> ""
            SingleCamMessageCommand.ICE -> ""
            SingleCamMessageCommand.LEAVE -> ""
            else -> throw IllegalArgumentException("지원하지 않는 기능입니다.")
        }
    }

}