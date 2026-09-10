package com.minispring2.aop;

import java.util.List;

public interface Advisor {

    /**
     * 执行增强逻辑
     */
    void enhance();

    List<Matcher> getMatcher();
}
