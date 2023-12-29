package com.mint.blinddate.config

import com.mint.blinddate.domain.chat.WebSocketChatHandler
import com.mint.blinddate.domain.single.SingleCamHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler

@Configuration
class WebSocketConfig(
    private val webSocketChatHandler: WebSocketChatHandler,
    private val singleCamHandler: SingleCamHandler,
) {

    @Bean
    fun webSocketHandlerMapping(): HandlerMapping {
        val map: MutableMap<String, WebSocketHandler> = HashMap()
        map["/chat"] = webSocketChatHandler
        map["/single"] = singleCamHandler
        return SimpleUrlHandlerMapping(map, -1)
    }

}