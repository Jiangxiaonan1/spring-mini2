package com.minispring2.web.core;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.lang.reflect.InvocationTargetException;

/**
 * @description
 * @create 2026-09-18 10:27:32
 **/
public interface HandlerAdapter {

    void handle(HttpRequest httpRequest, HttpResponse httpResponse, HandlerMethod handlerMethod) throws InvocationTargetException, IllegalAccessException, JsonProcessingException;
}
