package com.minispring2.web.core;

import com.minispring2.annotation.AnnotationUtils;
import com.minispring2.core.DefaultBeanFactory;
import com.minispring2.web.annotation.Controller;
import com.minispring2.web.annotation.RequestMapping;
import com.minispring2.web.common.HttpStatus;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @since 2026-09-18 09:41:47
 **/
public class HandlerMappingImpl implements HandlerMapping {

    public final Map<RequestMappingInfo, HandlerMethod> requestMappingInfoMap = new HashMap<>();

    @Override
    public HandlerMethod getHandlerMethodForPath(RequestMappingInfo requestMappingInfo) {
        for(RequestMappingInfo requestMappingInfoLoop : requestMappingInfoMap.keySet()) {
            if(Objects.equals(requestMappingInfoLoop.path, requestMappingInfo.path)
                    && ! Objects.equals(requestMappingInfoLoop.method, requestMappingInfo.method)) {
                return requestMappingInfoMap.get(requestMappingInfoLoop);
            }
        }
        return null;
    }

    @Override
    public HandlerMethod getHandlerMethod(RequestMappingInfo requestMappingInfo) {
        HandlerMethod handlerMethod = requestMappingInfoMap.get(requestMappingInfo);
        if(handlerMethod == null) {
            HandlerMethod handlerMethodForPath = getHandlerMethodForPath(requestMappingInfo);
            if(handlerMethodForPath == null) {
                HandlerMethod handlerMethod1 = new HandlerMethod();
                handlerMethod1.failStatus = HttpStatus.NOT_FOUND;
                return handlerMethod1;
            } else {
                HandlerMethod handlerMethod1 = new HandlerMethod();
                handlerMethod1.failStatus = HttpStatus.METHOD_NOT_ALLOWED;
                return handlerMethod1;
            }
        } else {
            return handlerMethod;
        }
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
                        method.getAnnotations();
                        if(AnnotationUtils.hasMeta(method.getAnnotations(), RequestMapping.class)) {
                            com.minispring2.web.annotation.RequestMapping annotation = method.getAnnotation(com.minispring2.web.annotation.RequestMapping.class);
                            if(annotation != null && annotation.value() != null) {
                                RequestMappingInfo requestMappingInfo = new RequestMappingInfo(annotation.value(), annotation.requestMethod());
                                if(requestMappingInfoMap.get(requestMappingInfo) != null) {
                                    throw new RuntimeException("MVC中出现重复的URL映射");
                                }
                                HandlerMethod handlerMethod = new HandlerMethod();
                                handlerMethod.methodName = method;
                                handlerMethod.object = o;
                                requestMappingInfoMap.put(requestMappingInfo, handlerMethod);
                            }
                        }
                    }
                }
            }
        }
    }

}
