package com.mint.blinddate.domain.cam

import com.fasterxml.jackson.databind.ObjectMapper
import com.mint.blinddate.domain.cam.CamMessageCommand.*
import com.mint.blinddate.service.cam.CamService
import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class CamHandler(
    private val objectMapper: ObjectMapper,
    private val camService: CamService,
) : WebSocketHandler {

    private val logger = KotlinLogging.logger {}

    override fun handle(session: WebSocketSession): Mono<Void> {
        logger.info { "message : $session" }
        val message = session.receive()
            .map {
                objectMapper.readValue(it.payloadAsText, CamMessage::class.java)
            }
            .log()

        return message.then()
    }

}