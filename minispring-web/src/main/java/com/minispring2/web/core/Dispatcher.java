package com.minispring2.web.core;

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

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse) throws InvocationTargetException, IllegalAccessException {
        HandlerAdapterImpl handlerAdapter = new HandlerAdapterImpl();
        handlerAdapter.handlerMapping = handlerMapping;
        handlerAdapter.handle(httpRequest, httpResponse);
    }
}
