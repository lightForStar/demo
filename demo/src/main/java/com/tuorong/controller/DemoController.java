package com.tuorong.controller;

import com.tuorong.model.Person;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Z先生 on 2019/10/22.
 */
@Controller
@RequestMapping("/test")
public class DemoController {
    @RequestMapping(value = "/getDemoPage")
    public ModelAndView getDemoPage(){
        List<Person> people = new ArrayList<>();
        Person p1 = new Person("1","张三",20);
        Person p2 = new Person("2","李四",30);
        people.add(p1);
        people.add(p2);
        ModelAndView modelAndView = new ModelAndView();
        //设置要返回的页面名称，省略.html后缀
        modelAndView.setViewName("index");
        //添加返回的数据，key为people，value为people集合
        modelAndView.addObject("people",people);
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping(value = "/getDemoAjax",method = RequestMethod.GET)
    public String getDemoAjax(){

        return "这是一个ajax发起的get请求";
    }

    @ResponseBody
    @RequestMapping(value = "/postDemoAjax",method = RequestMethod.POST)
    public String postDemoAjax(){

        return "这是一个ajax发起的post请求";
    }

    @ResponseBody
    @RequestMapping(value = "/ajaxRequest",method = RequestMethod.POST)
    public String ajaxRequest(){

        return "这是一个ajax发起的请求";
    }

}
