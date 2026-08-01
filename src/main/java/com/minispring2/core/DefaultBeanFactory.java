package com.minispring2.core;

import com.minispring2.demo.model.BeanDefinition;
import com.minispring2.demo.model.RuntimeBeanReference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 简易 Bean 工厂：根据 {@link com.minispring2.demo.model.BeanDefinition} 创建并缓存单例 Bean。
 *
 * @since 2026-07-28
 */
public class DefaultBeanFactory {

    private final Map<String, Object> singletonBeanMap = new HashMap<>();
    private final Map<String, Object> earlySingletonBeanMap = new HashMap<>();
    private final Map<String, ObjectFactory> beanFactory = new HashMap<>();
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
            System.out.println(beanName + "的创建需要访问一级缓存");
            return o;
        }
        Object o2 = earlySingletonBeanMap.get(beanName);
        if(o2 != null) {
            System.out.println(beanName + "的创建需要访问二级缓存");
            return o2;
        }
        ObjectFactory o3 = beanFactory.get(beanName);
        if(o3 != null) {
            System.out.println(beanName + "的创建需要访问三级缓存");
            Object object = o3.getObject();
            earlySingletonBeanMap.put(beanName, object);
            beanFactory.remove(beanName);
            return object;
        }
        return createBean(beanName);
    }

    public Object createBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if(beanDefinition == null) {
            throw new RuntimeException("未找到BeanDefinition：" + beanName);
        }
        System.out.println(beanName + "实例化");
        Object instantiate = instantiate(beanDefinition);
        beanFactory.put(beanName, () -> instantiate);
        System.out.println(beanName + "填充属性");
        populate(instantiate, beanDefinition);
        singletonBeanMap.put(beanName, instantiate);
        earlySingletonBeanMap.remove(beanName);
        beanFactory.remove(beanName);
        return instantiate;
    }

    public Object instantiate(BeanDefinition beanDefinition) {
        Class<?> aClass = beanDefinition.getaClass();
        try {
            return aClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void populate (Object o, BeanDefinition beanDefinition) {
        String setter = null;
        try {
            if(beanDefinition.getPropertyValueList() != null && !beanDefinition.getPropertyValueList().isEmpty()) {
                for (BeanDefinition.PropertyValue propertyValue : beanDefinition.getPropertyValueList()) {
                    System.out.println("填充的属性值字段名：" + propertyValue.getFieldName());
                    Object fieldValue = propertyValue.getFieldValue();
                    if(fieldValue instanceof RuntimeBeanReference) {
                        fieldValue = getBean(((RuntimeBeanReference) fieldValue).getBeanName());
                    }
                    setter = "set" + propertyValue.getFieldName().substring(0, 1).toUpperCase() + propertyValue.getFieldName().substring(1);
                    Method declaredMethod = o.getClass().getDeclaredMethod(setter, fieldValue.getClass());
                    declaredMethod.invoke(o, fieldValue);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("未找到目标方法：" + setter);
        }
    }
}
