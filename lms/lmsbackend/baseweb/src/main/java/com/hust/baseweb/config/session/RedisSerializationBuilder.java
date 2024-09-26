package com.hust.baseweb.config.session;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public final class RedisSerializationBuilder {

    public static <T> RedisTemplate<String, T> getSnappyRedisTemplate(
        final LettuceConnectionFactory factory,
        final Class<T> clazz
    ) {
        SnappyMsgPackRedisSerializer<T> snappyMsgPackSerializer = new SnappyMsgPackRedisSerializer<>(clazz);
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setDefaultSerializer(snappyMsgPackSerializer);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(snappyMsgPackSerializer);

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(snappyMsgPackSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    public static <T> RedisTemplate<String, T> getDefaultRedisTemplate(final LettuceConnectionFactory factory) {
        RedisTemplate<String, T> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(factory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
