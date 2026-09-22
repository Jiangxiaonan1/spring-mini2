package com.minispring2.web.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minispring2.annotation.AnnotationUtils;
import com.minispring2.web.annotation.RequestBody;
import com.minispring2.web.annotation.RequestParam;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 *
 * @since 2026-09-18 17:58:46
 **/
public class HandlerAdapterImpl implements HandlerAdapter {


    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse, HandlerMethod handlerMethod) throws InvocationTargetException, IllegalAccessException {
        Object object = handlerMethod.object;
        Method methodName = handlerMethod.methodName;
        ObjectMapper objectMapper = new ObjectMapper();

        Parameter[] parameters = methodName.getParameters();
        Object[] values = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (AnnotationUtils.hasMeta(parameter.getAnnotations(), RequestParam.class)) {
                RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                Object o = httpRequest.requestParams.get(annotation.value());
                if (o != null) {
                    Class<?> aClass = parameter.getType();
                    if (aClass == Integer.class || aClass == int.class) {
                        values[i] = Integer.valueOf(o.toString());
                    } else if (aClass == Long.class || aClass == long.class) {
                        values[i] = Long.valueOf(o.toString());
                    } else if (aClass == Boolean.class || aClass == boolean.class) {
                        values[i] = Boolean.valueOf(o.toString());
                    } else if (aClass == String.class || aClass == String.class) {
                        values[i] = o.toString();
                    } else {
                        throw new IllegalArgumentException("不支持的数据类型：" + aClass.getName());
                    }
                }

            } else if (AnnotationUtils.hasMeta(parameter.getAnnotations(), RequestBody.class)) {
                RequestBody annotation = parameter.getAnnotation(RequestBody.class);
                String requestBody = httpRequest.requestBody;
                if (annotation.required() && requestBody == null) {
                    throw new IllegalArgumentException("请求体参数为空");
                }
                if (!annotation.required() && requestBody == null) {
                    values[i] = null;
                } else {
                    Class<?> requestBodyType = parameter.getType();
                    Object o = null;
                    try {
                        o = objectMapper.readValue(requestBody, requestBodyType);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    values[i] = o;
                }
            }
        }
        Object result = methodName.invoke(object, values);
        if(result == null) {
            httpResponse.responseBody = null;
        } else if(result instanceof String) {
            httpResponse.responseBody = result.toString();
        } else {
            try {
                httpResponse.responseBody = objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
