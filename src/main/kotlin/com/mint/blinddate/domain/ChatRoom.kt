package com.mint.blinddate.domain

import java.util.*

data class ChatRoom(
    val name: String,
) {
    val id = UUID.randomUUID().toString()
}