package com.zgg.shiro.demo.demo.config.shiro;

import com.zgg.shiro.demo.demo.handler.ShiroUnauthorizeExceptionHandler;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {



    @Bean("securityManager")
    public DefaultWebSecurityManager securityManager() {
        System.out.println("securityManager1");
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // 设置realm.绑定到securityManager中，实际上认证和授权都是这个类调度的
        securityManager.setRealm(myRealm());
        //工具类绑定securityManager，会从这个工具类获取Subject
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }



    //配置拦截链，通过过滤器拦截请求进行认证授权
//    @Bean
//    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
//        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
//        chainDefinition.addPathDefinition("/**", "anon"); // all paths are managed via annotations
//
//        // logged in users with the 'admin' role
//        chainDefinition.addPathDefinition("/admin/**", "authc");
//
////        // logged in users with the 'document:read' permission
////        chainDefinition.addPathDefinition("/docs/**", "authc, perms[document:read]");
////
////        // all other paths require a logged in user
//
//        return chainDefinition;
//    }

    /**
     * ShiroFilter是整个Shiro的入口点，用于拦截需要安全控制的请求进行处理
     */
    @Bean("shiroFilter")
    public ShiroFilterFactoryBean shiroFilter() {
        System.out.println("shiroFilter");
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager());
        shiroFilter.setUnauthorizedUrl("/no-permission");             //没有权限默认跳转的页面
        Map<String, String> filterMap = new LinkedHashMap<>();
        filterMap.put("/**","anon");                    //关闭shiro
//        filterMap.put("/admin/admin/login.action", "anon");
//        filterMap.put("/admin/admin/logout","logout");
//        filterMap.put("/css/**", "anon");
//        filterMap.put("/fonts/**", "anon");
//        filterMap.put("/images/**", "anon");
//        filterMap.put("/js/**", "anon");
//        filterMap.put("/lib/**", "anon");
        filterMap.put("/admin/**","authc");
        shiroFilter.setFilterChainDefinitionMap(filterMap);
        return shiroFilter;
    }

    /**
     * 自定义身份认证 realm;
     * <p>
     * 必须写这个类，并加上 @Bean 注解，目的是注入 CustomRealm，
     * 否则会影响 CustomRealm类 中其他类的依赖注入
     */
    @Bean
    public MyRealm myRealm() {
        return new MyRealm();
    }

    /**
     * 开启Shiro的注解
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * 配置Shiro生命周期处理器
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public HandlerExceptionResolver solver(){
        System.out.println("HandlerExceptionResolver");
        return new ShiroUnauthorizeExceptionHandler();
    }


}
