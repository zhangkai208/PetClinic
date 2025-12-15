package com.zk.petclinic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
/**
 * 配置RedisTemplate Bean
 * @param factory Redis连接工厂
 * @return 配置好的RedisTemplate实例
 */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
    // 创建RedisTemplate实例
        RedisTemplate<String, Object> template = new RedisTemplate<>();
    // 设置Redis连接工厂
        template.setConnectionFactory(factory);
        // 序列化配置
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
