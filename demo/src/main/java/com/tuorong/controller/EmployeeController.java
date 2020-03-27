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
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * Created by Z先生 on 2019/11/8.
 */
@Controller
public class EmployeeController {
//    @Autowired
//    EmployeeMapper employeeMapper;
//
//    @RequestMapping("/page/employee")
//    public String toPageEmployee(){
//        return "emlpoyee-list";
//    }
//
//
//
//    @RequestMapping("/listEmployee")
//    @ResponseBody
//    public Result<Object> listEmployee(@RequestParam Integer page, @RequestParam Integer limit,String mobileOrName) throws IOException {
//        PageHelper.startPage(page,limit);
//        List<Employee> list = employeeMapper.listEmployee(mobileOrName);
//        return  ResultUtil.success(new PageInfo<>(list));
//    }

    @Autowired
    EmployeeMapper employeeMapper;

    @RequestMapping("/page/employee")
    public String toPageEmployee() {
        return "emlpoyee-list";
    }

    @RequestMapping("/page/editEmployee")
    public ModelAndView editEmployees(Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        modelAndView.addObject("employee", employee);
        modelAndView.setViewName("employee-edit");
        return modelAndView;
    }

    @RequestMapping("/page/detail")
    public ModelAndView detail(Integer id) {
        ModelAndView modelAndView = new ModelAndView();
        Employee employee = employeeMapper.selectByPrimaryKey(id);
        modelAndView.addObject("employee", employee);
        modelAndView.setViewName("employee-details");
        return modelAndView;
    }


    @RequestMapping("/listEmployee")
    @ResponseBody
    public Result<Object> listEmployee(@RequestParam Integer page, @RequestParam Integer limit, String mobileOrName) throws IOException {
        PageHelper.startPage(page, limit);

//         employeeMapper.listEmployee(mobileOrName);
        return ResultUtil.success(new PageInfo<>(employeeMapper.listEmployee(mobileOrName)));
    }

    @RequestMapping("/editEmployee.action")
    @ResponseBody
    public Result<Object> editEmployee(Employee data) {
        System.out.println(data);
        if (employeeMapper.updateByPrimaryKey(data) == 1) {
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }

    @RequestMapping("/delEmployee.action")
    @ResponseBody
    public Result<Object> delEmployee(Integer id) {
        System.out.println("id:" + id);
        if (employeeMapper.deleteByPrimaryKey(id) == 1) {
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }


    @RequestMapping("/page/addEmployee")
    public String addEmployeePage(){
        return "employee-add";
    }

    @RequestMapping("/addEmployee.action")
    @ResponseBody
    public Result<Object> addEmployee(Employee data){
        System.out.println(data);
        if(employeeMapper.insert(data)==1){
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }


}
