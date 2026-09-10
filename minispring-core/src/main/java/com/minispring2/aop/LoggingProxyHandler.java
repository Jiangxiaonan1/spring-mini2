package com.minispring2.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class LoggingProxyHandler implements InvocationHandler {

    private List<Advisor> advisorList;

    private Object target;

    public LoggingProxyHandler(List<Advisor> advisorList, Object target) {
        this.advisorList = advisorList;
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(advisorList != null && !advisorList.isEmpty()) {
            for (Advisor advisor : advisorList) {
                List<Matcher> matcherList = advisor.getMatcher();
                for (Matcher matcher : matcherList) {
                    if(matcher instanceof NameMatcher) {
                        if(matcher.match(method.getName())) {
                            advisor.enhance();
                        }
                    }
                }
            }
        }
        return method.invoke(target, args);
    }
}
