package com.hust.wmsbackend.management.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisCacheService {
    public static final String ALL_PRODUCTS_KEY = "PRODUCTS";
    public static final String ALL_WAREHOUSES_KEY = "WAREHOUSES";
    public static final int DEFAULT_EXPIRE_TIME_IN_MINUTES = 15;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String WMS_CACHE_PREFIX = "WMS:";

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void setCachedValueWithExpire(String key, Object object, int expireTimeInSec) {
        try {
            String string = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(WMS_CACHE_PREFIX + key, string, expireTimeInSec, TimeUnit.SECONDS);
            log.info(String.format("Set cached value for key %s", WMS_CACHE_PREFIX + key));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCachedValueWithExpire(String key, Object object) {
        try {
            String string = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(WMS_CACHE_PREFIX + key, string, DEFAULT_EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
            log.info(String.format("Set cached value for key %s", WMS_CACHE_PREFIX + key));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCachedString(String key) {
        return (String) redisTemplate.opsForValue().get(WMS_CACHE_PREFIX + key);
    }

    public <T> T getCachedObject(String key, Class<T> clazz) {
        try {
            Object objectInString = redisTemplate.opsForValue().get(WMS_CACHE_PREFIX + key);
            return objectMapper.convertValue(objectInString, clazz);
        } catch (Exception e) {
            log.warn(String.format("Can not get cache with key %s", key));
        }
        return null;
    }

    public <T> List<T> getCachedListObject(String key, Class<T> clazz) {
        try {
            String value = getCachedString(key);
            JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
            if (value != null) {
                return objectMapper.readValue(value, type);
            }
        } catch (Exception e) {
            log.warn(String.format("Can not get cache with key %s", key));
        }
        return null;
    }
}
