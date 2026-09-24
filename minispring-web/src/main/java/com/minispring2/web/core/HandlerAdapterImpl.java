package com.minispring2.web.core;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 *
 * @since 2026-09-18 17:58:46
 **/
public class HandlerAdapterImpl implements HandlerAdapter {


    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HandlerMethod handlerMethod) throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        Object object = handlerMethod.object;
        Method methodName = handlerMethod.methodName;
        Parameter[] parameters = methodName.getParameters();
        Object[] values = new Object[parameters.length];
        ArgumentResolver argumentResolver = new ArgumentResolver();
        argumentResolver.argumentResolver(httpRequest, parameters, values);
        Object result = methodName.invoke(object, values);
        ReturnValueResolver returnValueResolver = new ReturnValueResolver();
        returnValueResolver.returnValueResolver(httpResponse, result, methodName.getReturnType());
    }

}
