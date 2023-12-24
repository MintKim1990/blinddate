package com.mint.blinddate.domain

import org.springframework.stereotype.Component
import reactor.core.publisher.Sinks

@Component
class MessagePublisher {

    val sinks = Sinks.many().multicast().onBackpressureBuffer<String>()

    fun publish(message: String) = sinks.tryEmitNext(message)

}