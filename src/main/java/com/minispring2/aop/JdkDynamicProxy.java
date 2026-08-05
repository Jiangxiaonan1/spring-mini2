package com.minispring2.aop;

import java.lang.reflect.Proxy;
import java.util.List;

public class JdkDynamicProxy {

    public static Object getProxy(Object object, List<Advisor> advisorList) {

        return Proxy.newProxyInstance(
                object.getClass().getClassLoader(),
                object.getClass().getInterfaces(),
                new LoggingProxyHandler(advisorList, object)
        );
    }
}
