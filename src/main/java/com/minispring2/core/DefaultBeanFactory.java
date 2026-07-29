package com.minispring2.core;

import com.minispring2.demo.model.BeanDefinition;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @description
 * @create 2026-07-28 13:53:35
 **/
public class DefaultBeanFactory {

    private final Map<String, Object> singletonBeanMap = new HashMap<>();
    private final Map<String, Object> earlySingletonBeanMap = new HashMap<>();
    private final Map<String, Object> beanFactory = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

    public void addSingleton(String beanName, Object o) {
        singletonBeanMap.put(beanName, o);
    }

    public Object getSingleton(String beanName) {
        return singletonBeanMap.get(beanName);
    }

    public void addBeanDefinition (String beanDefinitionName, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(beanDefinitionName, beanDefinition);
    }

    public Object getBean(String beanName) {
        Object o = singletonBeanMap.get(beanName);
        if(o != null) {
            return o;
        }
        return createBean(beanName);
    }

    public Object createBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Object instantiate = instantiate(beanDefinition);
        singletonBeanMap.put(beanName, instantiate);
        return instantiate;
    }

    public Object instantiate(BeanDefinition beanDefinition) {
        Class aClass = beanDefinition.getaClass();
        try {
            Object o = aClass.getDeclaredConstructor().newInstance();
            if(beanDefinition.getPropertyValueList() != null) {
                for (BeanDefinition.PropertyValue propertyValue : beanDefinition.getPropertyValueList()) {
                    Method declaredMethod = aClass.getDeclaredMethod(propertyValue.getName(), propertyValue.getObject().getClass());
                    declaredMethod.invoke(o, propertyValue.getObject());
                }
            }
            return o;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

    }
}
