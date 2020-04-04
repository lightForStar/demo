package com.zgg.shiro.demo.demo.config.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.HashSet;
import java.util.Set;


public class MyRealm extends AuthorizingRealm {
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {

        System.out.println("开始执行认证");

        //获取输入的账号,这个是前端传给我们的token
        String username = authenticationToken.getPrincipal().toString();

        //这里构造正确的身份信息，用于shiro比对前端传进来的用户。可以从数据库查询，这里定死了，credentials是密码
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username,"123456","MyRealm");
        return simpleAuthenticationInfo;

    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("开始授权："+principalCollection.getPrimaryPrincipal().toString());
        //获取用户名,根据用户名查数据库，拿到该用户的角色信息,设置好role和permission
        String username =  principalCollection.getPrimaryPrincipal().toString();

        //这里设置的role对应检查@RequiresRoles注解标注的方法
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        //这里设置的permission对应检查@RequiresPermissions注解标注的方法
        Set<String> perminssion =new HashSet<>();
        perminssion.add("user_look");
        //这里根据用户查询他所拥有的角色和权限。
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();

        simpleAuthorizationInfo.setStringPermissions(perminssion);

        simpleAuthorizationInfo.setRoles(roles);
        return simpleAuthorizationInfo;
    }
}
