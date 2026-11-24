package com.minispring2.aop.matcher;

import java.util.List;

public class NameMatcher implements Matcher {

    private List<String> nameList;

    public NameMatcher(List<String> nameList) {
        this.nameList = nameList;
    }

    @Override
    public Boolean match(MatchInfo Matchinfo) {
        return nameList != null && nameList.contains(Matchinfo.getMethod().getName());
    }
}
