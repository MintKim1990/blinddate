package com.mint.blinddate.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@Controller
@RequestMapping("/chat/view")
class ChatViewController {

    @GetMapping
    fun view(model: Model) = Mono.just("view")

}