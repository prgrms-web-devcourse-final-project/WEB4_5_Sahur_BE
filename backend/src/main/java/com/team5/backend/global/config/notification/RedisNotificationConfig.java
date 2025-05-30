package com.team5.backend.global.config.notification;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.team5.backend.domain.notification.redis.NotificationEventMessage;

@Configuration
public class RedisNotificationConfig {

    @Bean(name = "notificationRedisTemplate")
    public RedisTemplate<String, NotificationEventMessage> notificationRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, NotificationEventMessage> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 직렬화 방식 지정
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(NotificationEventMessage.class));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(NotificationEventMessage.class));

        template.afterPropertiesSet();
        return template;

    }
}