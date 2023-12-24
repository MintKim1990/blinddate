package com.mint.blinddate.service

import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import reactor.core.publisher.Sinks

/**
 * Sinks.many()를 따로 작성 한 이유는 사용자가 채팅방을 빠져나갔을 경우
 * onCancel 메서드가 호출되는데 reactiveRedisMessageListenerContainer 내부에서
 * OnCancel 이벤트가 일어나면 구독하고있는 추가 사용자가 있더라도 Redis UnSubscribe
 * 명령어를 호출하여 메세지를 수신받지 못하는 버그가 존재
 *
 * 따라서 reactiveRedisMessageListenerContainer.receive에서 리턴하는 Flux를 그대로
 * 사용할 수 없어 Sinks.Many() 로 한번 래핑하여 Redis Publisher 구독은 해제되지 않게 유지
 *
 * Sinks.Many()에 구독한 사용자가 모두 없어질경우 Redis Publisher 구독도 취소되게
 * dispose() 메서드로 제어
 */
class ChatRoomMessageStream(
    chatRoomId: String,
    reactiveRedisMessageListenerContainer: ReactiveRedisMessageListenerContainer
) {

    private val sinks = Sinks.many().multicast().onBackpressureBuffer<String>()
    private val disposable = reactiveRedisMessageListenerContainer.receive(ChannelTopic(chatRoomId))
        .map { it.message }
        .doOnNext { sinks.tryEmitNext(it) }
        .subscribe()

    companion object {
        const val ONE = 1
    }

    fun asStream() = sinks.asFlux().doOnCancel { dispose() }

    fun isChatRoomEmpty() = sinks.currentSubscriberCount() == ONE

    fun dispose() {
        if (isChatRoomEmpty()) {
            disposable.dispose()
        }
    }

}