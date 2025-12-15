package com.zk.petclinic.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis工具类，提供Redis的基本操作方法
 * 使用Spring Data Redis的RedisTemplate进行Redis操作
 */
@Component
public class RedisUtil {
    // 自动注入RedisTemplate，用于执行Redis操作
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    //设置缓存
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean set(String key, Object value, long time) {
        try {
            redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //获取缓存
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //删除缓存
    public boolean delete(String key) {
        try {
            redisTemplate.delete(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
