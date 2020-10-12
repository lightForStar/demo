package com.zgg.redisdemo.web.controller;

import com.zgg.redisdemo.lock.RedisLock;
import com.zgg.redisdemo.model.User;
import com.zgg.redisdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private Integer store = 100;

    @RequestMapping("/add")
    public void add() {
        User user = new User();
        user.setId(7);
        user.setUsername("test");
        userService.add(user);
    }

    @RequestMapping("/delete/{id}")
    public void delete(@PathVariable("id") Integer id) {
        userService.deleteById(id);
    }

    @RequestMapping("/get/{id}")
    public User get(@PathVariable("id") Integer id) {

        User user = userService.getById(id);
        return user;
    }

    /**
     * 悲观锁使用demo
     *
     * @return
     */
    @RequestMapping("/order")
    public String order() {
        int orderId = 123456;
        String requestId = UUID.randomUUID().toString().replace("-", "");
        //获取锁
        boolean lock = redisLock.lock(Integer.toString(orderId), requestId, 60, TimeUnit.SECONDS);
//        boolean lock = stringRedisTemplate.opsForValue().setIfPresent(Integer.toString(orderId), requestId);
//        boolean lock = LockUtil.tryLock(redisTemplate,Integer.toString(orderId), requestId, 60, TimeUnit.SECONDS);


        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusSeconds(2);
        //如果加锁失败自旋两秒
        if (!lock) {
            while (now.isBefore(end)) {
                lock = redisLock.lock(Integer.toString(orderId), requestId, 60, TimeUnit.SECONDS);
                now = LocalDateTime.now();
                if (lock) {
                    break;
                }
            }
        }


        if (lock) {
            try {
                //            try {
//                //模拟业务操作 暂停0.5s
//                Thread.sleep(500);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
                if (store == 0) {
                    return "is finish";
                }
                store--;
                System.out.println(store);

                return "execute success";
            } finally {
                redisLock.unlock(Integer.toString(orderId), requestId);
//            stringRedisTemplate.delete(Integer.toString(orderId));
//            LockUtil.releaseLock(redisTemplate,Integer.toString(orderId), requestId);
            }

        } else {
            System.out.println("返回false");
            return "get lock fail";
        }
    }


    @RequestMapping("/set")
    public String set() {
        stringRedisTemplate.opsForValue().set("orderId", String.valueOf(100));
        return "ok";
    }

    /**
     * 乐观锁使用demo，先set，再使用jmeter进行并发测试
     *
     * @return
     */
    @RequestMapping("/optimize/order")
    public String optimizeOrder() {

        stringRedisTemplate.watch("orderId");
        int orderNumber = Integer.parseInt(Objects.requireNonNull(stringRedisTemplate.opsForValue().get("orderId")));
        stringRedisTemplate.setEnableTransactionSupport(true);
        //抢单，防止订单数量小于0
        if (orderNumber > 0) {
            stringRedisTemplate.multi();
            stringRedisTemplate.opsForValue().set("orderId", String.valueOf(orderNumber - 1));
            List<Object> exec = stringRedisTemplate.exec();
            System.out.println(orderNumber);
            System.out.println(exec);
            //执行成功则返回成功，执行失败不影响orderNumber的数量
            if (!CollectionUtils.isEmpty(exec) && (Boolean) exec.get(0)) {
                return "execute success";
            } else {
                return "execute error";
            }
        } else {
            System.out.println("返回false");
            return "get lock fail";
        }

    }

}
