package com.mint.blinddate.domain.single

data class SingleCamMessage(
    val from: String,
    val type: SingleCamMessageCommand,
    val data: String,
    var candidate: String?,
    var sdp: String?,
) {
}

enum class SingleCamMessageCommand {
    TEXT, OFFER, ANSWER, ICE, JOIN, LEAVE
}