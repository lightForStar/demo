package com.zgg.spring.session.demo.demo.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class HelloSpringSession {
    @RequestMapping(value = "/")
    public String test(HttpServletRequest httpServletRequest,Integer count){
        httpServletRequest.getSession().setAttribute("key",count);
        System.out.println(httpServletRequest.getSession().getId());
        return  httpServletRequest.getSession().getAttribute("key").toString();
    }

    @RequestMapping(value = "/getSession")
    public String getSession(HttpServletRequest httpServletRequest){
        System.out.println(httpServletRequest.getSession().getId());
        return  httpServletRequest.getSession().getAttribute("key").toString();
    }

}
