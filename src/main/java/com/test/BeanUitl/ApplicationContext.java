package com.test.BeanUitl;

import com.test.pojo.req.Person;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    static Map<String, Object> BeanMgr=new HashMap<>();

    public static Map<String, Object> getBeanMgr() {
        return BeanMgr;
    }

    public static void setBeanMgr(Map<String, Object> beanMgr) {
        BeanMgr = beanMgr;
    }
}
