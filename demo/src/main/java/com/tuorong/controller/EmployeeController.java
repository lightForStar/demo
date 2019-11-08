package com.tuorong.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tuorong.dao.EmployeeMapper;
import com.tuorong.model.Employee;
import com.tuorong.model.Result;
import com.tuorong.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;

/**
 * Created by Z先生 on 2019/11/8.
 */
@Controller
public class EmployeeController {
    @Autowired
    EmployeeMapper employeeMapper;

    @RequestMapping("/page/employee")
    public String toPageEmployee(){
        return "emlpoyee-list";
    }



    @RequestMapping("/listEmployee")
    @ResponseBody
    public Result<Object> listEmployee(@RequestParam Integer page, @RequestParam Integer limit,String mobileOrName) throws IOException {
        PageHelper.startPage(page,limit);
        List<Employee> list = employeeMapper.listEmployee(mobileOrName);
        return  ResultUtil.success(new PageInfo<>(list));
    }

}
