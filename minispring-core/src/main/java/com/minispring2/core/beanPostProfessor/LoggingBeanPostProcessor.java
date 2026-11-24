package com.minispring2.core.beanPostProfessor;

import com.minispring2.annotation.Component;
import com.minispring2.aop.CGlibDynamicProxy;
import com.minispring2.demo.model.UserInterface;

@Component
public class LoggingBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object afterInitialization(Object object) {
        if(! UserInterface.class.isAssignableFrom(object.getClass())) {
            return object;
        }
//        return JdkDynamicProxy.getProxy(object,new LoggingProxyHandler(object));
        CGlibDynamicProxy cGlibDynamicProxy = new CGlibDynamicProxy(object);
        return cGlibDynamicProxy.getProxy();
    }
}
