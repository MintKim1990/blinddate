package com.mint.blinddate.controller

import com.mint.blinddate.service.RoomService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/redis")
class RedisController(
    private val roomService: RoomService
) {

    @PostMapping("/room")
    fun room(@RequestParam name: String) = roomService.createRoom(name)

    @GetMapping("/room")
    fun findRooms() = roomService.findRooms()


}