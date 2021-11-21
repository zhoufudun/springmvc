package com.test.service;

import com.test.pojo.req.Person;
import com.test.springmvc.annotation.Service;

import java.util.HashMap;
import java.util.Map;

@Service("userService")
public class UserService {
    Map<String, Person> userMap=new HashMap<>();

    public UserService() {
        userMap.put("zfd",new Person("zfd","20"));
        userMap.put("zhangsan",new Person("zhangsan","30"));
        System.out.println("UserService init success-------------");
    }

    public Person getUserInfoByName(String name) {
        System.out.println("UserService get request, parameter name="+name);
        Person person = userMap.get(name);
//        System.out.println(person);
        return person;
    }
}
