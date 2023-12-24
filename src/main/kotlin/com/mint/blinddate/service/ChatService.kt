package com.mint.blinddate.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mint.blinddate.domain.ChatRoom
import org.springframework.stereotype.Service
import org.springframework.web.reactive.socket.WebSocketSession

@Service
class ChatService(
    private val objectMapper: ObjectMapper,
    private val chatRooms: LinkedHashMap<String, ChatRoom> = linkedMapOf()
) {

    fun findRooms() = ArrayList(chatRooms.values)

    fun findRoom(roomId: String) = chatRooms[roomId]

    fun createRoom(name: String) : ChatRoom {
        return ChatRoom(name = name).also { chatRooms[it.id] = it }
    }

    fun <T> sendMessage(session: WebSocketSession, message: T) {

    }

}