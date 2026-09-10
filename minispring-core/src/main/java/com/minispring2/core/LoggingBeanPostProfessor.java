package com.minispring2.core;

import com.minispring2.aop.JdkDynamicProxy;
import com.minispring2.aop.LoggingAdvisor;
import com.minispring2.aop.NameMatcher;

import java.util.Arrays;

public class LoggingBeanPostProfessor implements BeanPostProfessor{

    @Override
    public Object afterInitialization(Object object) {
        return JdkDynamicProxy.getProxy(object,
                Arrays.asList(
                        new LoggingAdvisor(
                                Arrays.asList(
                                        new NameMatcher(Arrays.asList("getName"))))));
    }
}
