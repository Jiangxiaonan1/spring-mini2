package com.minispring2.core;

import com.minispring2.demo.model.UserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Phase1：验证单例注册与获取。
 *
 * @since 2026-07-28
 */
public class Phase1Test {

    @Test
    public void getBeanTest() {
        UserService userService = new UserService();
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.addSingleton("userService", userService);
        Object userService1 = defaultBeanFactory.getSingleton("userService");

        assertSame(userService, userService1);

    }

}
