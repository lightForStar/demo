package com.tuorong.interceptor;

import com.tuorong.model.Admin;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Auther: Guo_zf
 * @Date: 2019/1/15 20:59
 * @Description:
 */
public class SessionAuthenticationInterceptor implements HandlerInterceptor {
    /**
     * 进入controller层之前拦截请求
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        try {
            Admin admin = (Admin) request.getSession().getAttribute("admin");
            if(admin==null ){
                System.out.println("session过期");
                throw new SessionValidationException("请重新登录");
            }
            return true;
        } catch (SessionValidationException e) {  //验证失败，返回false，不继续执行Controller中的方法
            response.sendRedirect(request.getContextPath()+"/demo/page/login");
            return false;
        }
    }

}
