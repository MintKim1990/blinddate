package com.mint.blinddate.domain.cam

import org.springframework.web.reactive.socket.WebSocketSession
import java.util.*
import kotlin.collections.HashMap

data class CamRoom(
    val name: String,
    val owner: String,
) {
    val id = UUID.randomUUID().toString() // 분산환경에서 Gateway 라우팅 식별키로도 사용 예정
    private val maxUser = 2
    private val users: HashMap<String, WebSocketSession> = hashMapOf()

    private fun isFull() = maxUser >= users.size

    fun join(name: String, session: WebSocketSession) {
        if (isFull()) {
            throw IllegalStateException("방 인원이 다 찼습니다")
        }
        users[name] = session
    }

}