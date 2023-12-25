package com.mint.blinddate.controller

import com.mint.blinddate.service.RoomService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/chat")
class ChatRestController(
    private val roomService: RoomService,
) {

    @PostMapping
    suspend fun createRoom(@RequestParam name: String) = roomService.createRoom(name)

}