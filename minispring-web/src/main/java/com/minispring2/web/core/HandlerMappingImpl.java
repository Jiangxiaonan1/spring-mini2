package com.minispring2.web.core;

import com.minispring2.core.DefaultBeanFactory;
import com.minispring2.web.annotation.Controller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @since 2026-09-18 09:41:47
 **/
public class HandlerMappingImpl implements HandlerMapping {

    public final Map<String, HandlerMethod> requestMappingMap = new HashMap<>();

    @Override
    public HandlerMethod getHandlerMethod(String requestUrl) {
        return requestMappingMap.get(requestUrl);
    }

    @Override
    public void autoAddHandlerMethod(DefaultBeanFactory defaultBeanFactory) {
        Map<String, Object> singletonBeanMap = defaultBeanFactory.getBeansWithAnnotation(Controller.class);
        for (String key : singletonBeanMap.keySet()) {
            Object o = singletonBeanMap.get(key);
            Class<?> aClass = o.getClass();
            if(aClass.isAnnotationPresent(Controller.class)) {
                Method[] declaredMethods = aClass.getDeclaredMethods();
                if(declaredMethods != null) {
                    for (Method method : declaredMethods) {
                        if(method.isAnnotationPresent(com.minispring2.web.annotation.RequestMapping.class)) {
                            com.minispring2.web.annotation.RequestMapping annotation = method.getAnnotation(com.minispring2.web.annotation.RequestMapping.class);
                            if(annotation != null && annotation.value() != null) {
                                if(requestMappingMap.get(annotation.value()) != null) {
                                    throw new RuntimeException("MVC中出现重复的URL映射");
                                }
                                HandlerMethod handlerMethod = new HandlerMethod();
                                handlerMethod.methodName = method;
                                handlerMethod.object = o;
                                requestMappingMap.put(annotation.value(), handlerMethod);
                            }
                        }
                    }
                }
            }
        }    }

}
