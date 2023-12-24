package com.mint.blinddate.controller

import com.mint.blinddate.domain.MessagePublisher
import com.mint.blinddate.service.ChatService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat/rest")
class ChatRestController(
    private val service: ChatService,
    private val messagePublisher: MessagePublisher
) {

    @PostMapping
    fun createRoom(@RequestParam name: String) = service.createRoom(name)

    @GetMapping
    fun findRooms() = service.findRooms()

    @PostMapping("/message")
    fun message(@RequestParam message: String) = messagePublisher.publish(message)

}