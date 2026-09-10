package com.minispring2.aop;

import java.util.List;

public class NameMatcher implements Matcher{

    private List<String> nameList;

    public NameMatcher(List<String> nameList) {
        this.nameList = nameList;
    }

    @Override
    public Boolean match(String name) {
        return nameList != null && nameList.contains(name);
    }
}
