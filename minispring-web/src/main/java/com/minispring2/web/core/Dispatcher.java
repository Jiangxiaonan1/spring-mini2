package com.minispring2.web.core;

import com.minispring2.web.annotation.Controller;
import com.minispring2.core.DefaultBeanFactory;
import com.minispring2.web.annotation.RequestMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @since 2026-09-09 11:24:14
 **/
public class Dispatcher {

    public final Map<String, HandlerMethod> requestMappingMap = new HashMap<>();

    public void init(DefaultBeanFactory defaultBeanFactory) {
        Map<String, Object> singletonBeanMap = defaultBeanFactory.singletonBeanMap;
        for (String key : singletonBeanMap.keySet()) {
            Object o = singletonBeanMap.get(key);
            Class<?> aClass = o.getClass();
            if(aClass.isAnnotationPresent(Controller.class)) {
                Method[] declaredMethods = aClass.getDeclaredMethods();
                if(declaredMethods != null) {
                    for (Method method : declaredMethods) {
                        if(method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                            if(annotation != null && annotation.value() != null) {
                                HandlerMethod handlerMethod = new HandlerMethod();
                                handlerMethod.methodName = method;
                                handlerMethod.object = o;
                                requestMappingMap.put(annotation.value(), handlerMethod);
                            }
                        }
                    }
                }
            }
        }
    }

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws InvocationTargetException, IllegalAccessException {
        if(requestMappingMap.get(httpRequest.requestUrl) != null) {
            HandlerMethod handlerMethod = requestMappingMap.get(httpRequest.requestUrl);
            Object object = handlerMethod.object;
            Method methodName = handlerMethod.methodName;
            Object result = null;
            try {
                result = methodName.invoke(object);
            } catch (Exception e) {
                throw e;
            }
            httpResponse.responseBody = result.toString();
        } else {
            httpResponse.responseBody = "404";
        }
    }
}
