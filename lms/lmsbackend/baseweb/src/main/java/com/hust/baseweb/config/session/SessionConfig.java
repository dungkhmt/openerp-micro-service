package com.hust.baseweb.config.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.data.redis.RedisSessionRepository;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

import java.time.Duration;

@Configuration
@EnableSpringHttpSession
public class SessionConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private Integer port;

    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        config.setPassword(password);

        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return RedisSerializationBuilder.getDefaultRedisTemplate(redisConnectionFactory());
    }

    @Bean
    public RedisOperations<String, Object> redisOperations() {
        return RedisSerializationBuilder.getSnappyRedisTemplate(redisConnectionFactory(), Object.class);
    }

    @Bean
    public RedisSessionRepository sessionRepository(RedisTemplate<String, Object> redisTemplate) {
        RedisSessionRepository redisSessionRepository = new RedisSessionRepository(redisTemplate);
        redisSessionRepository.setDefaultMaxInactiveInterval(Duration.ofHours(24));
        return redisSessionRepository;
    }

    /**
     * Customize Spring Session’s HttpSession integration to use HTTP headers X-Auth-Token
     * to convey the current session information instead of cookies,
     * <a href="https://docs.spring.io/spring-session/docs/current/reference/html5/guides/java-rest.html">reference</a>
     *
     * @return
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }
}
