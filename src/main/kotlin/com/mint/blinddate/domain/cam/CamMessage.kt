package com.mint.blinddate.domain.cam

data class CamMessage(
    val from: String,
    val command: CamMessageCommand,
    val data: String,
    var candidate: String?,
    var sdp: String?,
) {
}

enum class CamMessageCommand {
    TEXT, OFFER, ANSWER, ICE, JOIN, LEAVE
}