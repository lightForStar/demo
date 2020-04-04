package com.zgg.shiro.demo.demo.handler;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ShiroUnauthorizeExceptionHandler implements HandlerExceptionResolver {
    //使setUnauthorizedUrl（没有权限默认跳转的页面）起作用
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        //如果是shiro无权操作，因为shiro 在操作auno等一部分不进行转发至无权限url
        if(e instanceof UnauthorizedException){
            ModelAndView mv = new ModelAndView("no");
            return mv;
        }
        //未认证会走这里
        e.printStackTrace();
        ModelAndView mv = new ModelAndView("no");
        mv.addObject("exception", e.toString().replaceAll("\n", "<br/>"));
        return mv;
    }
}
