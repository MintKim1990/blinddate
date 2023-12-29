package com.mint.blinddate.controller

import com.mint.blinddate.service.cam.CamService
import com.mint.blinddate.service.chat.ChatService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/view")
class ViewController(
    private val chatService: ChatService,
    private val camService: CamService,
) {

    @GetMapping
    suspend fun view(model: Model): String {
        model.addAttribute("rooms", chatService.findRooms())
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

    @ResponseBody
    @PostMapping("/room/chat")
    suspend fun createChatRoom(@RequestParam name: String) =
        chatService.createRoom(name)

    @ResponseBody
    @PostMapping("/room/cam")
    suspend fun createRoom(@RequestParam name: String,
                           @RequestParam owner: String) =
        camService.createRoom(name, owner)

    @GetMapping("/cam")
    suspend fun cam(model: Model): String {
        return "cam"
    }

}