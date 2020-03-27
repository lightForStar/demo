package com.tuorong.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuorong.dao.AdminMapper;
import com.tuorong.model.Admin;
import com.tuorong.model.Result;
import com.tuorong.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    AdminMapper adminMapper;

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

    @RequestMapping("/login.action")
    @ResponseBody
    public Result<Object> login(@RequestParam String loginname, @RequestParam String loginpwd, HttpSession httpSession) {
        System.out.println("loginname:"+loginname+";loginpwd:"+loginpwd);
        Admin admin = adminMapper.selectAdminByName(loginname);
        if (admin!=null ) {
            System.out.println(admin);
            if(admin.getLoginpwd().equals(loginpwd)){
                httpSession.setAttribute("admin",adminMapper.selectAdminByName(loginname));
                    System.out.println("登录成功");
                    return ResultUtil.success();
            }
        }
        return ResultUtil.fail("账号或密码错误");
    }

    @RequestMapping("/page/admin")
    public String toPageAdmin(){
        return "admin-list";
    }

    @RequestMapping("/page/addAdmin")
    public ModelAndView addAdmin(){
        ModelAndView modelAndView = new ModelAndView();
        Admin admin = new Admin();
        modelAndView.addObject("admin",admin);
        modelAndView.setViewName("admin-add");
        return modelAndView;
    }

    @RequestMapping("/page/editAdmin")
    public ModelAndView editAdmins(Integer id){
        ModelAndView modelAndView = new ModelAndView();
        Admin admin = adminMapper.selectByPrimaryKey(id);
        modelAndView.addObject("admin",admin);
        modelAndView.setViewName("admin-edit");
        return modelAndView;
    }

    @RequestMapping("/adminPage/detail")
    public ModelAndView detail(Integer id){
        ModelAndView modelAndView = new ModelAndView();
        Admin admin = adminMapper.selectByPrimaryKey(id);
        modelAndView.addObject("admin",admin);
        modelAndView.setViewName("admin-details");
        return modelAndView;
    }

    @RequestMapping("/listAdmin")
    @ResponseBody
    public Result<Object> listAdmin(@RequestParam Integer page, @RequestParam Integer limit,String mobileOrName) throws IOException {
        PageHelper.startPage(page,limit);
        System.out.println("列表");
        List<Admin> list = adminMapper.listAdmin(mobileOrName);
        return  ResultUtil.success(new PageInfo<>(list));
    }

    @RequestMapping("/addAdmin.action")
    @ResponseBody
    public Result<Object> addAdmin(Admin data){
        System.out.println(data);
        if(adminMapper.insert(data)==1){
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }

    @RequestMapping("/editAdmin.action")
    @ResponseBody
    public Result<Object> editAdmin(Admin data){
        System.out.println(data);
        if(adminMapper.updateByPrimaryKey(data)==1){
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }

    @RequestMapping("/delAdmin.action")
    @ResponseBody
    public Result<Object> delAdmin(Integer id){
        System.out.println("id:"+id);
        if(adminMapper.deleteByPrimaryKey(id)==1){
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }


}
