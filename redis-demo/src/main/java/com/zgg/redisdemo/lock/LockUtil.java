package com.zgg.redisdemo.lock;

import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * @author : Z先生
 * @date : 2020-10-11 19:24
 **/
public class LockUtil {
    private static final String RELEASE_LOCK_LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    public static boolean tryLock(RedisTemplate redisTemplate, String lockKey, String lockValue, long expire, TimeUnit timeUnit){
        RedisCallback<Boolean> callback = (connection) -> connection.set(lockKey.getBytes(StandardCharsets.UTF_8),lockValue.getBytes(StandardCharsets.UTF_8), Expiration.seconds(timeUnit.toSeconds(expire)), RedisStringCommands.SetOption.SET_IF_ABSENT);
        return (boolean) redisTemplate.execute(callback);
    }

    public static boolean releaseLock(RedisTemplate redisTemplate, String lockKey, String lockValue){
        RedisCallback<Boolean> callback = (connection) -> connection.eval(RELEASE_LOCK_LUA_SCRIPT.getBytes(StandardCharsets.UTF_8), ReturnType.BOOLEAN,1,lockKey.getBytes(StandardCharsets.UTF_8),lockValue.getBytes(StandardCharsets.UTF_8));
        return (boolean) redisTemplate.execute(callback);
    }

}
