package com.minispring2.aop.advisor;

import com.minispring2.aop.matcher.Matcher;

import java.util.Collections;
import java.util.List;

/**
 *
 * @since 2026-11-18 15:59:28
 **/
public class AuthAdvisor implements Advisor{
    /**
     *
     */
    @Override
    public void enhance() {

    }

    /**
     * @return
     */
    @Override
    public List<Matcher> getMatcher() {
        return Collections.emptyList();
    }
}
