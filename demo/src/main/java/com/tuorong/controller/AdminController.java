package com.tuorong.controller;

import com.tuorong.model.Result;
import com.tuorong.utils.ResultUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Z先生 on 2019/11/8.
 */
@Controller
public class AdminController {
    @RequestMapping("/page/index")
    public String toPageIndex(){
        return "index-page";
    }

    @RequestMapping("/page/login")
    public String toPageLogin(){
        return "login";
    }

    @RequestMapping("/")
    public String toLogin(){
        return "login";
    }

    @RequestMapping("/page/welcome")
    public String toWelcomePage(){
        return "welcome";
    }



    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Result<Object> login(@RequestParam String username, @RequestParam String password)  {
        return ResultUtil.success();
    }
}
