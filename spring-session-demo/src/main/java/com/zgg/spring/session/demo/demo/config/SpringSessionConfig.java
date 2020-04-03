package com.zgg.spring.session.demo.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
public class SpringSessionConfig {

    /**
     * 更换序列化器
     * @return
     */
    @Bean("springSessionDefaultRedisSerializer")
    public RedisSerializer setSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }


    //Cookie配置
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        String cookieName = "JSESSIONID"; //更改cookie中session的key名称，默认是SESSION
        cookieSerializer.setCookieName(cookieName);//sessionId名称
        return  cookieSerializer;
    }

    //HttpSessionId配置
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        CookieHttpSessionIdResolver cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();
        cookieHttpSessionIdResolver.setCookieSerializer(cookieSerializer());
        return cookieHttpSessionIdResolver;
    }


}
