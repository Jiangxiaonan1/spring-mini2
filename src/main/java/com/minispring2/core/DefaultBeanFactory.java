package com.minispring2.core;

import com.minispring2.demo.model.BeanDefinition;
import com.minispring2.demo.model.RuntimeBeanReference;

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
        if(beanDefinition == null) {
            throw new RuntimeException("未找到BeanDefinition：" + beanName);
        }
        Object instantiate = instantiate(beanDefinition);
        singletonBeanMap.put(beanName, instantiate);
        return instantiate;
    }

    public Object instantiate(BeanDefinition beanDefinition) {
        Class aClass = beanDefinition.getaClass();
        try {
            Object o = aClass.getDeclaredConstructor().newInstance();
            if(beanDefinition.getPropertyValueList() != null && beanDefinition.getPropertyValueList().size() > 0) {
                for (BeanDefinition.PropertyValue propertyValue : beanDefinition.getPropertyValueList()) {
                    Object fieldValue = propertyValue.getFieldValue();
                    if(fieldValue instanceof RuntimeBeanReference) {
                        fieldValue = getBean(((RuntimeBeanReference) fieldValue).getBeanName());
                    }
                    Method declaredMethod = aClass.getDeclaredMethod("set" + propertyValue.getFieldName().substring(0, 1).toUpperCase() + propertyValue.getFieldName().substring(1, propertyValue.getFieldName().length()), fieldValue.getClass());
                    declaredMethod.invoke(o, fieldValue);
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
