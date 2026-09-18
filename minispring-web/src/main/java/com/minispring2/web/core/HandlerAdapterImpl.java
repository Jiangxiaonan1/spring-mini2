package com.minispring2.web.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @since 2026-09-18 17:58:46
 **/
public class HandlerAdapterImpl implements HandlerAdapter {

    HandlerMapping handlerMapping;

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws InvocationTargetException, IllegalAccessException {
        HandlerMethod handlerMethod = handlerMapping.getHandlerMethod(httpRequest.requestUrl);
        if(handlerMethod != null) {
            Object object = handlerMethod.object;
            Method methodName = handlerMethod.methodName;
            Object result = null;
            result = methodName.invoke(object);
            httpResponse.responseBody = result.toString();
        } else {
            httpResponse.responseBody = "404";
        }
    }
}
