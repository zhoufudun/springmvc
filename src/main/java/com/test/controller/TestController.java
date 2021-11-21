package com.test.controller;

import com.test.dao.UserDao;
import com.test.pojo.req.Person;
import com.test.springmvc.annotation.Autowired;
import com.test.springmvc.annotation.Controller;
import com.test.springmvc.annotation.RequestMapping;
import com.test.springmvc.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserDao userDao;

    @RequestMapping("/hello")
    public String hello(HttpServletResponse response){
        try {
            response.getWriter().write("test hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    @RequestMapping("/hello2")
    public String hello2(HttpServletResponse response,
                         @RequestParam String name,
                         @RequestParam String age){
        try {
            response.getWriter().write("hello "+name+",your age is "+age);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    @RequestMapping("/hello3")
    public String hello3(HttpServletResponse response,
                         @RequestParam(value = "name") String name,
                                 @RequestParam Integer age){
        try {
            response.getWriter().write("hello "+name+",your age is "+age);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    @RequestMapping("/getUserInfo")
    public String getFromSql(HttpServletResponse response,
                         @RequestParam(value = "name") String name){
        try {
            Person person = userDao.getUserInfoByName(name);
            response.getWriter().write(person.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
