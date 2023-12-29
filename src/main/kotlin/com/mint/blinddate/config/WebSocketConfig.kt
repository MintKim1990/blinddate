package com.mint.blinddate.config

import com.mint.blinddate.domain.chat.ChatHandler
import com.mint.blinddate.domain.cam.CamHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler

@Configuration
class WebSocketConfig(
    private val chatHandler: ChatHandler,
    private val camHandler: CamHandler,
) {

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        val map: MutableMap<String, WebSocketHandler> = HashMap()
        map["/chat"] = chatHandler
        map["/cam"] = camHandler
        return SimpleUrlHandlerMapping(map, -1)
    }

}