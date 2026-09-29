package com.minispring2.web.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.minispring2.core.DefaultBeanFactory;
import com.minispring2.web.common.HttpStatus;

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

    public void doService(HttpRequest httpRequest, HttpResponse httpResponse)  {
        HandlerMethod handlerMethod = handlerMapping.getHandlerMethod(httpRequest.requestMappingInfo);
        if(handlerMethod.failStatus != null) {
            httpResponse.status = handlerMethod.failStatus.value();
            httpResponse.responseBody = String.valueOf(handlerMethod.failStatus.value());
            return;
        }

        HandlerAdapterImpl handlerAdapter = new HandlerAdapterImpl();
        try {
            handlerAdapter.handle(httpRequest, httpResponse, handlerMethod);
        } catch (Exception e) {
            writeError(httpResponse, unwrap(e));
        }
    }

    private Throwable unwrap(Throwable error) {
        Throwable cause = error;
        while (cause instanceof InvocationTargetException) {
            cause = ((InvocationTargetException) cause).getTargetException();
        }
        return cause;
    }

    private void writeError(HttpResponse httpResponse, Throwable cause) {
        httpResponse.contentType = "text/plain;charset=UTF-8";
        if (cause instanceof IllegalArgumentException) {
            httpResponse.status = HttpStatus.WRONG_PARAM.value();
            httpResponse.responseBody = cause.getMessage() != null ? cause.getMessage() : "bad request";
        } else if (cause instanceof JsonProcessingException) {
            httpResponse.status = HttpStatus.WRONG_PARAM.value();
            httpResponse.responseBody = "bad json";
        } else {
            httpResponse.status = HttpStatus.INTERNAL_ERROR.value();
            httpResponse.responseBody = "internal error";
        }
    }
}
