package com.minispring2.core;

import com.minispring2.core.beanPostProfessor.LoggingBeanPostProcessor;
import com.minispring2.core.model.BeanDefinition;
import com.minispring2.demo.dao.UserDao;
import com.minispring2.demo.model.UserInterface;
import com.minispring2.demo.model.UserService;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * 动态代理
 *
 */
public class Phase4Test {

    @Test
    public void test() {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        LoggingBeanPostProcessor loggingBeanPostProfessor = new LoggingBeanPostProcessor();
        defaultBeanFactory.addBeanPostProfessor(loggingBeanPostProfessor);

        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setaClass(UserService.class);
        BeanDefinition.PropertyValue propertyValue = new BeanDefinition.PropertyValue();
        propertyValue.setFieldName("name");
        propertyValue.setFieldValue("苹果");
        beanDefinition.setPropertyValueList(Arrays.asList(propertyValue));
        BeanDefinition beanDefinition1 = new BeanDefinition();
        beanDefinition1.setaClass(UserDao.class);

        defaultBeanFactory.addBeanDefinition("userService", beanDefinition);
        defaultBeanFactory.addBeanDefinition("userDao", beanDefinition1);

        UserInterface userService = (UserInterface) defaultBeanFactory.getBean("userService");
        System.out.println(userService);
        System.out.println(userService.getName());
    }
}
