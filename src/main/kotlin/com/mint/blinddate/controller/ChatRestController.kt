package com.mint.blinddate.controller

import com.mint.blinddate.service.chat.ChatRoomService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatRestController(
    private val chatRoomService: ChatRoomService,
) {

    @PostMapping
    suspend fun createRoom(@RequestParam name: String) = chatRoomService.createRoom(name)

}