package com.minispring2.core;

import com.minispring2.demo.dao.UserDao;
import com.minispring2.demo.model.BeanDefinition;
import com.minispring2.demo.model.RuntimeBeanReference;
import com.minispring2.demo.model.UserService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Phase2Test {

    @Test
    public void Test() {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setaClass(UserService.class);

        BeanDefinition.PropertyValue propertyValue = new BeanDefinition.PropertyValue();
        propertyValue.setFieldName("userDao");
        propertyValue.setFieldValue(new RuntimeBeanReference("userDao"));
        BeanDefinition.PropertyValue propertyValue2 = new BeanDefinition.PropertyValue();
        propertyValue2.setFieldName("name");
        propertyValue2.setFieldValue("草稚京");
        beanDefinition.setPropertyValueList(Arrays.asList(propertyValue, propertyValue2));

        BeanDefinition beanDefinition2 = new BeanDefinition();
        beanDefinition2.setaClass(UserDao.class);

        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.addBeanDefinition("userService", beanDefinition);
        defaultBeanFactory.addBeanDefinition("userDao", beanDefinition2);


        UserService userService = (UserService) defaultBeanFactory.getBean("userService");
        assertNotNull(userService);
        assertNotNull(userService.getName());
        assertNotNull(userService.getUserDao());

        System.out.println("name=" + userService.getName());
        System.out.println("userDao=" + userService.getUserDao());

    }
}
