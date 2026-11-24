package com.minispring2.aop.matcher;

import java.lang.reflect.Method;

/**
 * 用于是否需要代理的判断条件的数据媒介
 * @since 2026-11-18 16:12:38
 **/
public class MatchInfo {
    private Object target;
    private Method method;
    private Object[] args;

    public MatchInfo(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
