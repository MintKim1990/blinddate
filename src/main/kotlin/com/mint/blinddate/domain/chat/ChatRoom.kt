package com.mint.blinddate.domain.chat

import java.util.*

data class ChatRoom(
    val name: String,
) {
    val id = UUID.randomUUID().toString()
}