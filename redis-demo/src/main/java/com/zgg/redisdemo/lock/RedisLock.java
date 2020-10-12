package com.zgg.redisdemo.lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author : Z先生
 * @date : 2020-10-11 15:55
 **/
@Component
public class RedisLock {
    /** 释放锁lua脚本 */
    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean lock(String key, String value, long expire, TimeUnit timeUnit){
        //setIfAbsent相当于redis中的set key vlaue nx px命令
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expire, timeUnit);
    }


    public boolean unlock(String key, String value){
        // 指定 lua 脚本，并且指定返回值类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(RELEASE_LOCK_LUA_SCRIPT,Long.class);
        // 参数一：redisScript，参数二：key列表，参数三：arg（可多个）
        Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(key),value);
        return Objects.nonNull(result) && result > 0;
    }



}
