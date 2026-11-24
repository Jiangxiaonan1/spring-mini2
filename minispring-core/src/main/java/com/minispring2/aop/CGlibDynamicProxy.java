package com.minispring2.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @since 2026-11-24 09:26:59
 **/
public class CGlibDynamicProxy implements MethodInterceptor {

    private final Object target;

    public CGlibDynamicProxy(Object target) {
        this.target = target;
    }

    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(target.getClass());
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("start -> 生成代理：" + obj.getClass() + "，方法调用：" + method.getName());
        Object o = methodProxy.invokeSuper(obj, args);
        System.out.println("end -> 生成代理：" + obj.getClass() + "，方法调用：" + method.getName());
        return o;
    }
}
