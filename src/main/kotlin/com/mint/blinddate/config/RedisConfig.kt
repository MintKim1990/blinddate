package com.mint.blinddate.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun reactiveRedisMessageListenerContainer(reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory) =
        ReactiveRedisMessageListenerContainer(reactiveRedisConnectionFactory)

    @Primary
    @Bean
    fun reactiveRedisTemplate(reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, String> {

        val redisSerializationContext = RedisSerializationContext.newSerializationContext<String, String>()
            .key(StringRedisSerializer())
            .value(StringRedisSerializer())
            .hashKey(StringRedisSerializer())
            .hashValue(StringRedisSerializer())
            .build()

        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, redisSerializationContext)
    }

}