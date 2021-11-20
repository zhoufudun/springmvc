package com.test.controller;

import com.test.springmvc.annotation.Controller;
import com.test.springmvc.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Controller
@RequestMapping("/test2")
public class Test2Controller {
    @RequestMapping("/hello")
    public String hello(HttpServletRequest request, HttpServletResponse response){
        try {
            response.getWriter().write("test hello");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
