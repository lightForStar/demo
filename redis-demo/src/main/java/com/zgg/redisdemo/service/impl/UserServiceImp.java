package com.zgg.redisdemo.service.impl;

import com.zgg.redisdemo.model.User;
import com.zgg.redisdemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImp implements UserService {

    private static Map<Integer,User> userMap;

    //模拟数据库操作
    static {
        userMap = new HashMap<>();
        userMap.put(1,new User(1,"张三"));
        userMap.put(2,new User(2,"李四"));
        userMap.put(3,new User(3,"王五"));
    }

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    /**
     *   Cacheable的cacheNames属性设置缓存的名称
     * @param id
     * @return
     */
    @Override
    @Cacheable(cacheNames = "user", key = "#id")
    public User getById(Integer id) {
        log.info("getById，id：{}",id);
        User user = userMap.get(id);
        //如果为空缓存一个固定值
        if (Objects.isNull(user)){
            user = new User(-1,"null");
        }
        return user;
    }

    /**
     *  使用RedisTemplate缓存
     * @param id
     * @return
     */
    @Override
    public User getByIdWithRedisTemplate(Integer id) {
        //从缓存中获取
        User user = (User) redisTemplate.opsForValue().get("user");
        if (Objects.isNull(user)){
            //为空则从数据库中获取
            user = userMap.get(id);
            //如果为空缓存一个固定值
            if (Objects.isNull(user)){
                user = new User(-1,"null");
            }
            //缓存两个小时，这里可以加随机数防止缓存雪崩
            redisTemplate.opsForValue().set("user",user,2, TimeUnit.HOURS);
        }

        return user;
    }


    /**
     * 删除数据库中的值并且删除缓存
     * @param id
     */
    @Override
    @CacheEvict(cacheNames="user", key = "#id")
    public void deleteById(Integer id) {
        userMap.remove(id);
    }

    /**
     * 通过入参新增缓存
     * @param user
     */
    @CachePut(cacheNames ="user", key = "#user.id")
    @Override
    public void add(User user) {
        userMap.put(user.getId(),user);
    }

    /**
     * 通过入参更新缓存
     * @param user
     */
    @Override
    @CachePut(cacheNames ="user", key = "#user.id")
    public void update(User user) {
        userMap.put(user.getId(),user);
    }
}
