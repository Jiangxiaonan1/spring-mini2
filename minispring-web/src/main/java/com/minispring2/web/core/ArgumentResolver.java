package com.minispring2.web.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minispring2.annotation.AnnotationUtils;
import com.minispring2.web.annotation.PathVariable;
import com.minispring2.web.annotation.RequestBody;
import com.minispring2.web.annotation.RequestParam;

import java.lang.reflect.Parameter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @since 2026-09-23 09:26:15
 **/
public class ArgumentResolver {

    public void argumentResolver(HttpRequest httpRequest, Parameter[] parameters, Object[] values) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (AnnotationUtils.hasMeta(parameter.getAnnotations(), RequestParam.class)) {
                RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                Object o = httpRequest.requestParams.get(annotation.value());
                if (o != null) {
                    extracted(values, parameter, i, o);
                }

            } else if (AnnotationUtils.hasMeta(parameter.getAnnotations(), PathVariable.class)) {
                Pattern p = Pattern.compile(httpRequest.requestMappingInfo.regexPath);
                Matcher m = p.matcher(httpRequest.requestMappingInfo.path);
                while (m.find()) {
                    String destinationString = m.group(1);
                    values[i] = destinationString;
                    extracted(values, parameter, i, destinationString);
                    System.out.println(destinationString);
                    break;
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
                    values[i] = objectMapper.readValue(requestBody, requestBodyType);
                }
            }
        }
    }

    private static void extracted(Object[] values, Parameter parameter, int i, Object o) {
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

}
