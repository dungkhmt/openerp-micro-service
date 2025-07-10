package openerp.openerpresourceserver.cache;

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
    public static final String ALL_ORDER_KEY = "ORDERS";
    public static final String ALL_HUB_KEY = "HUBS";
    public static final String ALL_ROUTE_KEY = "ROUTES";

    public static final int DEFAULT_EXPIRE_TIME_IN_MINUTES = 15;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final String SMDELI_CACHE_PREFIX = "SMDELI:";

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void setCachedValueWithExpire(String key, Object object, int expireTimeInSec) {
        try {
            String string = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(SMDELI_CACHE_PREFIX + key, string, expireTimeInSec, TimeUnit.SECONDS);
            log.info(String.format("Set cached value for key %s", SMDELI_CACHE_PREFIX + key));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCachedValueWithExpire(String key, Object object) {
        try {
            String string = objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(SMDELI_CACHE_PREFIX + key, string, DEFAULT_EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
            log.info(String.format("Set cached value for key %s", SMDELI_CACHE_PREFIX + key));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCachedString(String key) {
        return (String) redisTemplate.opsForValue().get(SMDELI_CACHE_PREFIX + key);
    }

    public <T> T getCachedObject(String key, Class<T> clazz) {
        try {
            Object objectInString = redisTemplate.opsForValue().get(SMDELI_CACHE_PREFIX + key);
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

    /**
     * -> THÊM MỚI: Phương thức để xóa một key khỏi cache.
     * Rất quan trọng để đảm bảo tính nhất quán của dữ liệu.
     * @param key Key cần xóa (chưa có prefix).
     */
    public void deleteKey(String key) {
        String fullKey = SMDELI_CACHE_PREFIX + key;
        redisTemplate.delete(fullKey);
        log.info("Invalidated/Deleted cache for key {}", fullKey);
    }
}