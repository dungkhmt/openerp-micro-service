package wms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import wms.common.CommonResource;

@Configuration
class RedisConfig {

    @Autowired
    private CommonResource commonResource;

    @Bean
    public JedisPool jedisPool() {
        final JedisPoolConfig poolConfig = buildPoolConfig();
        return new JedisPool(poolConfig, commonResource.getRedisHost(), commonResource.getRedisPort());
    }

    // NOTE: Due to use multi threading and sharing Singleton JedisClient
    // We have to use a pool of jedis clients
    // This pool is thread safe and reliable, as long as we return the resource to the pool when we're done with it.
    // Ref: https://www.baeldung.com/jedis-java-redis-client-library#Connection
    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        // TODO: Modify setting if need, otherwise use default settings
//        poolConfig.setMaxTotal(128);
//        poolConfig.setMaxIdle(128);
//        poolConfig.setMinIdle(16);
//        poolConfig.setTestOnBorrow(true);
//        poolConfig.setTestOnReturn(true);
//        poolConfig.setTestWhileIdle(true);
//        poolConfig.setNumTestsPerEvictionRun(3);

        // Must set block when all thread busy
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }
}