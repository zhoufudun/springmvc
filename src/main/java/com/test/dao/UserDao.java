package com.test.dao;

import com.test.pojo.req.Person;
import com.test.service.UserService;
import com.test.springmvc.annotation.Autowired;
import com.test.springmvc.annotation.Component;
import com.test.springmvc.annotation.Service;

import java.util.HashMap;
import java.util.Map;

@Component("userDao")
public class UserDao {

    @Autowired
    UserService userService;

    public Person getUserInfoByName(String name) {
        return userService.getUserInfoByName(name);
    }
}
