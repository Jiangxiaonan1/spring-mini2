package com.minispring2.core;

import com.minispring2.annotation.AnnotationUtils;
import com.minispring2.demo.model.BeanDefinition;
import com.minispring2.demo.model.RuntimeBeanReference;
import com.minispring2.support.Scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简易 Bean 工厂：根据 {@link com.minispring2.demo.model.BeanDefinition} 创建并缓存单例 Bean。
 *
 * @since 2026-07-28
 */
public class DefaultBeanFactory {

    public final Map<String, Object> singletonBeanMap = new HashMap<>();
    private final Map<String, Object> earlySingletonBeanMap = new HashMap<>();
    private final Map<String, ObjectFactory> beanFactory = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final List<BeanPostProfessor> beanPostProfessorList = new ArrayList<>();

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

    public Object getBeanForType(Class<?> clazz) {
        for (String beanName : singletonBeanMap.keySet()) {
            Object object = singletonBeanMap.get(beanName);
            /**
             * A.class.isAssignableFrom(B.class)
             *
             * 从 A 的视角读：
             *
             * A 能否接收一个来自 B 的值？
             */
            if(clazz.isAssignableFrom(object.getClass())) {
                return object;
            }
        }

        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(clazz.isAssignableFrom(beanDefinition.getaClass())) {
                return createBean(beanDefinition.getBeanName());
            }
        }

        return null;
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
        Object initialize = initialize(instantiate);
        singletonBeanMap.put(beanName, initialize);
        earlySingletonBeanMap.remove(beanName);
        beanFactory.remove(beanName);

        return initialize;
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
        try {
            if(beanDefinition.getPropertyValueList() != null && !beanDefinition.getPropertyValueList().isEmpty()) {
                for (BeanDefinition.PropertyValue propertyValue : beanDefinition.getPropertyValueList()) {
                    System.out.println("填充的属性值字段名：" + propertyValue.getFieldName());
                    if("userService".equals(propertyValue.getFieldName())) {
                        System.out.println(1);
                    }
                    Object fieldValue = propertyValue.getFieldValue();
                    if(fieldValue instanceof RuntimeBeanReference) {
                        fieldValue = getBeanForType(((RuntimeBeanReference) fieldValue).getaClass());
                    }

                    for (Field field : o.getClass().getDeclaredFields()) {
                        if(field.getName().equals(propertyValue.getFieldName())) {
                            field.setAccessible(true);
                            field.set(o, fieldValue);
                            break;
                        }
                    }

                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void addBeanPostProfessor(BeanPostProfessor beanPostProfessor) {
        beanPostProfessorList.add(beanPostProfessor);
    }

    private Object initialize (Object object) {
        for (BeanPostProfessor beanPostProfessor : beanPostProfessorList) {
            object = beanPostProfessor.beforeInitialization(object);
            object = beanPostProfessor.afterInitialization(object);
        }
        return object;
    }

    public void scan(String... basePackages) {
        Scan scan = new Scan(this.getClass().getClassLoader(), beanDefinitionMap);
        for (String basePackage : basePackages) {
            scan.scan(basePackage);
        }

    }

    public void preInstantiateSingletons() {
        for (String beanName : beanDefinitionMap.keySet()) {
            getBean(beanName);
        }
    }

    public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> aclass) {
        Map<String, Object> stringObjectMap = new HashMap<>();
        for (String key : singletonBeanMap.keySet()) {
            Object o = singletonBeanMap.get(key);
            if(AnnotationUtils.hasMeta(o.getClass().getAnnotations(), aclass)) {
                stringObjectMap.put(key, o);
            }
        }

        return stringObjectMap;
    }

}
