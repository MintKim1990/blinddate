package com.mint.blinddate.controller

import com.mint.blinddate.service.RoomService
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@Controller
@RequestMapping("/chat")
class ChatViewController(
    private val roomService: RoomService
) {

    @GetMapping("/view")
    suspend fun view(model: Model): String {
        model.addAttribute("rooms", roomService.findRooms())
        return "index"
    }

    @GetMapping("/join")
    suspend fun join(@RequestParam chatRoomId: String,
             @RequestParam name: String,
             model: Model): String {
        model.addAttribute("chatRoomId", chatRoomId)
        model.addAttribute("name", name)
        return "view"
    }

}