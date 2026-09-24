package com.minispring2.web.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.minispring2.core.DefaultBeanFactory;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @since 2026-09-09 11:24:14
 **/
public class Dispatcher {

    HandlerMapping handlerMapping;

    public void init(DefaultBeanFactory defaultBeanFactory){
        handlerMapping = new HandlerMappingImpl();
        handlerMapping.autoAddHandlerMethod(defaultBeanFactory);
    }

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws InvocationTargetException, IllegalAccessException, JsonProcessingException {
        HandlerMethod handlerMethod = handlerMapping.getHandlerMethod(httpRequest.requestMappingInfo);
        if(handlerMethod.failStatus != null) {
            httpResponse.status = handlerMethod.failStatus.value();
            httpResponse.responseBody = String.valueOf(handlerMethod.failStatus.value());
            return;
        }

        HandlerAdapterImpl handlerAdapter = new HandlerAdapterImpl();
        handlerAdapter.handle(httpRequest, httpResponse, handlerMethod);
    }
}
