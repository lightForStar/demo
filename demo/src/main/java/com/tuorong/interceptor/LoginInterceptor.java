package com.tuorong.interceptor;

import com.tuorong.model.Admin;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Z先生 on 2019/11/19.
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Admin admin =  (Admin) request.getSession().getAttribute("admin");
        if (admin == null){
          //  response.sendRedirect("/demo/page/login");
            return true;
        }

        System.out.println(request.getContextPath());

        return true;
    }
}
