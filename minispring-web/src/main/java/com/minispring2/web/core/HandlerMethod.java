package com.minispring2.web.core;

import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @since 2026-09-09 11:25:38
 **/
public class HandlerMethod {
    public Object object;
    public Method methodName;
    public Map<String ,Object> requestParams;
}
