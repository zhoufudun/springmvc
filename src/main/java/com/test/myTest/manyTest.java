package com.test.myTest;

import com.test.controller.Test2Controller;
import com.test.service.UserService;

public class manyTest {
    public static void main(String[] args) {
        try {
            Class<?> aClass =
                    Class.forName(Test2Controller.class.getCanonicalName());
            System.out.println(aClass);
            System.out.println(Test2Controller.class.getCanonicalName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        UserService userService = new UserService();
        System.out.println(userService.getUserInfoByName("zfd"));
    }
}
