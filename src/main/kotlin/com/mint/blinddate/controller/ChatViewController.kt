package com.mint.blinddate.controller

import com.mint.blinddate.service.chat.ChatRoomService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/chat")
class ChatViewController(
    private val chatRoomService: ChatRoomService
) {

    @GetMapping("/view")
    suspend fun view(model: Model): String {
        model.addAttribute("rooms", chatRoomService.findRooms())
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

    @GetMapping("/single")
    suspend fun single(model: Model): String {
        return "single"
    }

}