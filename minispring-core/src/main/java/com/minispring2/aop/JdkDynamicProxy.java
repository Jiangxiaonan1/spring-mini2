package com.minispring2.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class JdkDynamicProxy {

    public static Object getProxy(Object object, InvocationHandler invocationHandler) {

        return Proxy.newProxyInstance(
                object.getClass().getClassLoader(),
                object.getClass().getInterfaces(),
                invocationHandler
        );
    }
}
