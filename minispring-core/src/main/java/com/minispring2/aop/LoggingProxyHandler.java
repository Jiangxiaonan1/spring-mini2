package com.minispring2.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LoggingProxyHandler implements InvocationHandler {

    private Object target;

    public LoggingProxyHandler() {

    }

    public LoggingProxyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long startTime = System.nanoTime();
        Object invoke = method.invoke(target, args);
        long endTime = System.nanoTime();
        System.out.println(target.getClass().getName() + "."+ method.getName() + "方法总耗时：" + (endTime - startTime) / 1000000 + "秒");
        return invoke;
    }
}
