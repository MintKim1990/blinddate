package com.mint.blinddate.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler

@Configuration
class WebSocketConfig(
    private val webSocketHandler: WebSocketHandler,
) {

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        val map: MutableMap<String, WebSocketHandler> = HashMap()
        map["/ws/chat"] = webSocketHandler
        return SimpleUrlHandlerMapping(map, -1)
    }

}