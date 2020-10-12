# Redis实战

## 应用场景

Redis是一个内存性的数据库，由于Redis具有很高的性能，我们经常使用其做缓存层提高我们项目的并发能力。

Redis提供了key的过期时间功能，我们也常使用这个功能用于验证码的过期时间

由于Redis是单线程的，天然是线程安全的，可以利用这个特性做分布式锁



## 缓存

利用Redis做缓存主要有两种方式

1、使用 Spring Cache 集成 Redis

2、直接使用RedisTemplate

### 使用 Spring Cache 集成 Redis

- 集成redis的依赖

```xml
        <!--        redis start-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!--        用作 redis 连接池，如不引入启动会报错-->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
        <!--        redis end-->
```

- 自定义RedisTemplate模板，以支持其他数据类型

```java
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
```

- 配置CacheManager统一管理缓存配置

```java
    @Bean
    public CacheManager cacheManager(RedisTemplate<String, Object> template) {

        // 基本配置
        RedisCacheConfiguration defaultCacheConfiguration =
                RedisCacheConfiguration
                        .defaultCacheConfig()
                        // 设置key为String
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getStringSerializer()))
                        // 设置value 为自动转Json的Object
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(template.getValueSerializer()))
                        // 不缓存null
                        .disableCachingNullValues()
                        // 缓存数据保存1小时
                        .entryTtl(Duration.ofHours(1));

        // 够着一个redis缓存管理器
        RedisCacheManager redisCacheManager =
                RedisCacheManager.RedisCacheManagerBuilder
                        // Redis 连接工厂
                        .fromConnectionFactory(template.getConnectionFactory())
                        // 缓存配置
                        .cacheDefaults(defaultCacheConfiguration)
                        // 配置同步修改或删除 put/evict
                        .transactionAware()
                        //配置key为user的数据过期时间
                        .withCacheConfiguration("user",getCacheConfigurationWithTtl(template,20))
                        .build();

        return redisCacheManager;
    }
```

- 使用@Cachable、@CachePut、@CacheEvict完成缓存操作

```java
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
     * Cacheable的cacheNames属性设置缓存的名称
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
```

- 自定义配置key的过期时间

```java
        // 够着一个redis缓存管理器
        RedisCacheManager redisCacheManager =
                RedisCacheManager.RedisCacheManagerBuilder
                        // Redis 连接工厂
                        .fromConnectionFactory(template.getConnectionFactory())
                        // 缓存配置
                        .cacheDefaults(defaultCacheConfiguration)
                        // 配置同步修改或删除 put/evict
                        .transactionAware()
                        //配置key为user的数据过期时间
                        .withCacheConfiguration("user",getCacheConfigurationWithTtl(template,20))
                        .build();
```



#### 缓存注解

1、`@Cacheable` 根据方法的请求参数对其结果进行缓存

- key：缓存的 key，可以为空，如果指定要按照 SPEL 表达式编写，如果不指定，则按照方法的所有参数进行组合。
- value：缓存的名称，必须指定至少一个（如 @Cacheable (value='user')或者@Cacheable(value={'user1','user2'})）
- condition：缓存的条件，可以为空，使用 SPEL 编写，返回 true 或者 false，只有为 true 才进行缓存。

> `@Cacheable` 注解不支持配置过期时间，所有需要通过配置 **cacheManager**来配置默认的过期时间和针对每个类或者是方法进行缓存失效时间配置。

2、`@CachePut`根据方法的请求参数对其结果进行缓存，和@Cacheable 不同的是，它每次都会触发真实方法的调用。参数描述见上。

3、`@CacheEvict`根据条件对缓存进行清空

- key：同上
- value：同上
- condition：同上
- allEntries：是否清空所有缓存内容，缺省为 false，如果指定为 true，则方法调用后将立即清空所有缓存
- beforeInvocation：是否在方法执行前就清空，缺省为 false，如果指定为 true，则在方法还没有执行的时候就清空缓存。缺省情况下，如果方法执行抛出异常，则不会清空缓存。

### 使用RedisTemplate做缓存

```java
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
```

## 分布式锁

### 悲观锁

**悲观锁**是基于一种悲观的态度类来防止一切数据冲突，它是以一种预防的姿态在修改数据之前把数据锁住，然后再对数据进行读写，在它释放锁之前任何人都不能对其数据进行操作，直到前面一个人把锁释放后下一个人数据加锁才可对数据进行加锁，然后才可以对数据进行操作

#### redis使用悲观锁实例

```java
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

```

**工具类**

```java
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

```

#### 原理

使用redis中的set key vlaue nx px命令，如果key不存在则设置，为了避免死锁使用finally释放以及设置过期时间

**设置value的作用**

假设有这样一种场景，线程A获取了key为order的分布式锁，执行时间超过了过期时间，这时候线程B进来拿到了key为order的分布式锁，此时线程A刚好执行完成，退出的时候删除了key为order的分布式锁，如果不加以判断就会将线程B的锁删除，导致锁保护失效，此时我们在删除key之前先判断key对应的value是否和我们之前设置的一致，如果一致则删除，这样就不会误删。

**使用lua脚本删除key**

我们知道redis的命令操作是不保证原子性的，想要保证原子性就必须使用lua脚本

### 乐观锁

**乐观锁**是对于数据冲突保持一种乐观态度，操作数据时不会对操作的数据进行加锁（这使得多个任务可以并行的对数据进行操作），只有到数据提交的时候才通过一种机制来验证数据是否存在冲突(一般实现方式是通过加版本号然后进行版本号的对比方式实现);

#### redis使用乐观锁实例

```java
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
```

#### 原理

**watch命令的实现**

在每个代表数据库的 `redis.h/redisDb` 结构类型中， 都保存了一个 `watched_keys` 字典， 字典的键是这个数据库被监视的键， 而字典的值则是一个链表， 链表中保存了所有监视这个键的客户端。

![](https://lightforstar.oss-cn-shenzhen.aliyuncs.com/blog/graphviz-9aea81f33da1373550c590eb0b7ca0c2b3d38366.svg)

[WATCH](http://redis.readthedocs.org/en/latest/transaction/watch.html#watch) 命令的作用， 就是将**当前客户端和要监视的键在 `watched_keys` 中进行关联**。

举个例子， 如果当前客户端为 `client10086` ， 那么当客户端执行 `WATCH key1 key2` 时， 前面展示的 `watched_keys` 将被修改成这个样子：

![](https://lightforstar.oss-cn-shenzhen.aliyuncs.com/blog/graphviz-fe5e31054c282a3cdd86656994fe1678a3d4f201.svg)

通过 `watched_keys` 字典， 如果程序想检查某个键是否被监视， 那么它只要检查字典中是否存在这个键即可； 如果程序要获取监视某个键的所有客户端， 那么只要取出键的值（一个链表）， 然后对链表进行遍历即可。

**WATCH 的触发**

在任何对数据库键空间（key space）进行修改的命令成功执行之后 （比如 [FLUSHDB](http://redis.readthedocs.org/en/latest/server/flushdb.html#flushdb) 、 [SET](http://redis.readthedocs.org/en/latest/string/set.html#set) 、 [DEL](http://redis.readthedocs.org/en/latest/key/del.html#del) 、 [LPUSH](http://redis.readthedocs.org/en/latest/list/lpush.html#lpush) 、 [SADD](http://redis.readthedocs.org/en/latest/set/sadd.html#sadd) 、 [ZREM](http://redis.readthedocs.org/en/latest/sorted_set/zrem.html#zrem) ，诸如此类）， `multi.c/touchWatchedKey` 函数都会被调用 —— 它检查数据库的 `watched_keys` 字典， 看是否有客户端在监视已经被命令修改的键， 如果有的话， 程序将所有监视这个/这些被修改键的客户端的 `REDIS_DIRTY_CAS` 选项打开：

![](https://lightforstar.oss-cn-shenzhen.aliyuncs.com/blog/graphviz-e5c66122242aa10939b696dfeeb905343c5202bd.svg)

当客户端发送 **EXEC** 命令、触发事务执行时， 服务器会对客户端的状态进行检查：

- 如果客户端的 `REDIS_DIRTY_CAS` 选项已经被打开，那么说明被客户端监视的键至少有一个已经被修改了，事务的安全性已经被破坏。服务器会放弃执行这个事务，直接向客户端返回空回复，表示事务执行失败。
- 如果 `REDIS_DIRTY_CAS` 选项没有被打开，那么说明所有监视键都安全，服务器正式执行事务。

举个例子，假设数据库的 `watched_keys` 字典如下图所示：

![](https://lightforstar.oss-cn-shenzhen.aliyuncs.com/blog/graphviz-9aea81f33da1373550c590eb0b7ca0c2b3d38366.svg)

如果某个客户端对 `key1` 进行了修改（比如执行 `DEL key1` ）， 那么所有监视 `key1` 的客户端， 包括 `client2` 、 `client5` 和 `client1` 的 `REDIS_DIRTY_CAS` 选项都会被打开， 当客户端 `client2` 、 `client5` 和 `client1` 执行 [EXEC](http://redis.readthedocs.org/en/latest/transaction/exec.html#exec) 的时候， 它们的事务都会以失败告终。

最后，当一个客户端结束它的事务时，无论事务是成功执行，还是失败， `watched_keys` 字典中和这个客户端相关的资料都会被清除。

## 测试

使用大于100个线程并发访问order或optimize/order，观察到数量按顺序递减，并且不会出现负数

jmeter的配置：

![image-20201012112231995](https://lightforstar.oss-cn-shenzhen.aliyuncs.com/blog/image-20201012112231995.png)

![image-20201012112250090](https://lightforstar.oss-cn-shenzhen.aliyuncs.com/blog/image-20201012112250090.png)

[文件链接](https://lightforstar.oss-cn-shenzhen.aliyuncs.com/file/HTTP%20Request.jmx)

## 参考

[链接一](https://redisbook.readthedocs.io/en/latest/feature/transaction.html)

[链接二](https://mp.weixin.qq.com/s?__biz=Mzg2OTA0Njk0OA==&mid=2247486396&idx=1&sn=72417f3b8e21e878f64a4f10ddc10340&chksm=cea24477f9d5cd613a90e9472a76d55476461b4607c97311d04d38ea4fa073e2da021dbdd3ba&mpshare=1&scene=1&srcid=&sharer_sharetime=1584526459548&sharer_shareid=cb5a6d3d4b4a0a46dd0a8a8b3f84dc08&key=362c475b03eb9012ef1d534ff79cb8cb4309f7382a927a89dd09ca267aadaa6761758cd43c2acd767a3a2dd1908e2d4c0cdfa9c0e9d7e09abfb4df1bd2701263d3ac67c7585b702d358d94c2882092d6&ascene=1&uin=MTM2NzM2MTczOQ%3D%3D&devicetype=Windows+10&version=62080079&lang=zh_CN&exportkey=A%2FUsH%2F3z7iU2mnDOGgXz7uA%3D&pass_ticket=EC6JgWZp7JAPbinzrEtMZiOdDNAYbtzkRaV8e9VepSUMf8JoTuuc1TyVzi5ltuFq)

