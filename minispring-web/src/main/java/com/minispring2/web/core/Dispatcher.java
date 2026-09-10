package com.minispring2.web.core;

import com.minispring2.annotation.Controller;
import com.minispring2.core.DefaultBeanFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @since 2026-09-09 11:24:14
 **/
public class Dispatcher {

    public final Map<String, RequestMapping> requestMappingMap = new HashMap<>();

    public void init(DefaultBeanFactory defaultBeanFactory) {
        Map<String, Object> singletonBeanMap = defaultBeanFactory.singletonBeanMap;
        for (String key : singletonBeanMap.keySet()) {
            Object o = singletonBeanMap.get(key);
            if(o.getClass().isAnnotationPresent(Controller.class)) {
                requestMappingMap.
            }
        }
    }

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws InvocationTargetException, IllegalAccessException {
        if(requestMappingMap.get(httpRequest.requestUrl) != null) {
            RequestMapping requestMapping = requestMappingMap.get(httpRequest.requestUrl);
            Object object = requestMapping.object;
            Method methodName = requestMapping.methodName;
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
