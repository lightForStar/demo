/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.zgg.shiro.demo.demo.web.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class LoginController {


    @RequestMapping("/login.html")
    public String loginTemplate() {

        return "login";
    }

    @ResponseBody
    @RequestMapping("/login")
    public String login(String username,String password) {
        System.out.println("开始登陆");
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);

        try {
            subject.login(token);
        }catch (AuthenticationException exception){
            System.out.println(token.getPassword());
            return "认证失败";
        }

        return "登录成功";

    }

    @ResponseBody
    @RequiresRoles("admin")
    @RequestMapping("/admin")
    public String admin() {

        return "admin角色访问成功";

    }


    @RequestMapping("/no-permission")
    public String no() {
        return "no";

    }

}
