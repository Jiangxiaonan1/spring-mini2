package com.minispring2.core;

import com.minispring2.demo.dao.UserDao;
import com.minispring2.demo.model.BeanDefinition;
import com.minispring2.demo.model.UserService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class Phase2Test {

    @Test
    public void Test() {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setaClass(UserService.class);

        BeanDefinition.PropertyValue propertyValue = new BeanDefinition.PropertyValue();
        propertyValue.setName("setUserDao");
        propertyValue.setObject(new UserDao());
        beanDefinition.setPropertyValueList(Arrays.asList(propertyValue));

        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.addBeanDefinition("userService", beanDefinition);


        Object userService = defaultBeanFactory.getBean("userService");
        assertNotNull(userService);

    }
}
