package com.mint.blinddate.domain.chat

import java.util.*

data class ChatRoom(
    val name: String,
) {
    val id = UUID.randomUUID().toString() // 분산환경에서 Gateway 라우팅 식별키로도 사용 예정
}