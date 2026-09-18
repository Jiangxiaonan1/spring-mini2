package com.minispring2.web.core;

import com.minispring2.core.DefaultBeanFactory;

/**
 * @description
 * @create 2026-09-18 09:39:59
 **/
public interface HandlerMapping {

    HandlerMethod getHandlerMethod(String requestUrl);

    void autoAddHandlerMethod(DefaultBeanFactory defaultBeanFactory);
}
