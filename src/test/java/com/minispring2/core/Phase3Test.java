package com.minispring2.core;

import com.minispring2.demo.model.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Phase3Test {

    @Test
    public void test() {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setaClass(Fruit.class);
        BeanDefinition beanDefinition2 = new BeanDefinition();
        beanDefinition2.setaClass(Apple.class);
        BeanDefinition beanDefinition3 = new BeanDefinition();
        beanDefinition3.setaClass(Banana.class);


        BeanDefinition.PropertyValue propertyValue1 = new BeanDefinition.PropertyValue();
        propertyValue1.setFieldName("apple");
        propertyValue1.setFieldValue(new RuntimeBeanReference("apple"));
        BeanDefinition.PropertyValue propertyValue2 = new BeanDefinition.PropertyValue();
        propertyValue2.setFieldName("banana");
        propertyValue2.setFieldValue(new RuntimeBeanReference("banana"));
        BeanDefinition.PropertyValue propertyValue3 = new BeanDefinition.PropertyValue();
        propertyValue3.setFieldName("fruit");
        propertyValue3.setFieldValue(new RuntimeBeanReference("fruit"));

        beanDefinition.setPropertyValueList(Arrays.asList(propertyValue1, propertyValue2));
        beanDefinition2.setPropertyValueList(Arrays.asList(propertyValue3));
        beanDefinition3.setPropertyValueList(Arrays.asList(propertyValue3));

        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.addBeanDefinition("fruit", beanDefinition);
        defaultBeanFactory.addBeanDefinition("apple", beanDefinition2);
        defaultBeanFactory.addBeanDefinition("banana", beanDefinition3);

        Fruit fruit = (Fruit) defaultBeanFactory.getBean("fruit");
        assertNotNull(fruit);
        System.out.println(fruit);

    }

}
