package com.minispring2.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * @create 2026-07-28 13:53:35
 **/
public class DefaultBeanFactory {

    private final Map<String, Object> singletonBeanMap = new HashMap<>();
    private final Map<String, Object> earlySingletonBeanMap = new HashMap<>();
    private final Map<String, Object> beanFactory = new HashMap<>();

    public void addSingleton(String beanName, Object o) {
        singletonBeanMap.put(beanName, o);
    }

    public Object getSingleton(String beanName) {
        return singletonBeanMap.get(beanName);
    }

}
