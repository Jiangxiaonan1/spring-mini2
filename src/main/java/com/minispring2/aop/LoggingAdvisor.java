package com.minispring2.aop;

import java.util.List;

public class LoggingAdvisor implements Advisor{

    private List<Matcher> matcherList;

    public LoggingAdvisor(List<Matcher> matcherList) {
        this.matcherList = matcherList;
    }

    @Override
    public void enhance() {
        System.out.println(this.getClass().getName() + "日志打印");
    }

    @Override
    public List<Matcher> getMatcher() {
        return matcherList;
    }

}
