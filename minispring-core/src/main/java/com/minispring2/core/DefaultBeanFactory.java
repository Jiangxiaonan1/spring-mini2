package com.minispring2.core;

import com.minispring2.annotation.AnnotationUtils;
import com.minispring2.annotation.Autowired;
import com.minispring2.annotation.Primary;
import com.minispring2.core.beanPostProfessor.BeanPostProcessor;
import com.minispring2.core.model.BeanDefinition;
import com.minispring2.core.model.RuntimeBeanReference;
import com.minispring2.support.Scan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 简易 Bean 工厂：根据 {@link BeanDefinition} 创建并缓存单例 Bean。
 *
 * @since 2026-07-28
 */
public class DefaultBeanFactory {

    private final Map<String, Object> singletonBeanMap = new HashMap<>();
    private final Map<String, Object> earlySingletonBeanMap = new HashMap<>();
    private final Map<String, ObjectFactory> beanFactory = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

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
        return getBean(getBeanNameForType(clazz));

    }

    public List<String> getBeanNamesForType(Class<?> clazz) {
        List<String> beanNames = new ArrayList<>();
        for (String beanName : beanDefinitionMap.keySet()) {
            if(clazz.isAssignableFrom(beanDefinitionMap.get(beanName).getaClass())) {
                beanNames.add(beanName);
            }
        }
        return beanNames;
    }

    public String getBeanNameForType(Class<?> clazz) {
        Map<String, Class> map = new HashMap<>();
        for (String beanName : beanDefinitionMap.keySet()) {
            Class<?> aClass = beanDefinitionMap.get(beanName).getaClass();
            if(clazz.isAssignableFrom(aClass)) {
                map.put(beanName, aClass);
            }
        }
        Map<String, Class> resultMap = new HashMap<>();
        if(map.size() > 1) {
            for (String beanName : map.keySet()) {
                Class aClass = map.get(beanName);
                if(AnnotationUtils.hasMeta(aClass.getAnnotations(), Primary.class)) {
                    resultMap.put(beanName, aClass);
                }
            }
            if(resultMap.isEmpty()) {
                throw new RuntimeException(clazz.getName() + "存在多个Bean，未用Primary指定哪一个");
            }
            if(resultMap.size() > 1) {
                throw new RuntimeException(clazz.getName() + "存在多个相同类型的Bean使用了Primary注解");
            }

            return resultMap.keySet().iterator().next();

        } else if (map.size() == 0) {
            throw new RuntimeException("未找到该类型的Bean，" + clazz.getName());
        } else {
            return map.keySet().iterator().next();
        }
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
        if("userController".equals(beanName)) {
            System.out.println("userController -> populate");
        }
        injectAutowiredFields(instantiate);
        Object initialize = initialize(instantiate);
        singletonBeanMap.put(beanName, initialize);
        earlySingletonBeanMap.remove(beanName);
        beanFactory.remove(beanName);

        return initialize;
    }

    public void injectAutowiredFields(Object bean) {

        for (Field field : bean.getClass().getDeclaredFields()) {
            try {
                if(AnnotationUtils.hasMeta(field.getAnnotations(), Autowired.class)) {
                    field.setAccessible(true);
                    field.set(bean, getBeanForType(field.getType()));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public Object instantiate(BeanDefinition beanDefinition) {
        Class<?> aClass = beanDefinition.getaClass();
        try {
            Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
            Class[] parameterTypes = null;
            for (Constructor constructor : declaredConstructors) {
                if (AnnotationUtils.hasMeta(constructor.getAnnotations(), Autowired.class)) {
                    if(parameterTypes != null) {
                        throw new RuntimeException("多个构造方法，未指定哪一个");
                    }
                    parameterTypes = constructor.getParameterTypes();
                }
            }
            if(parameterTypes != null) {
                List<Object> inputObjects = new ArrayList();
                for (Class clazz : parameterTypes) {
                    inputObjects.add(getBeanForType(clazz));
                }
                return aClass.getDeclaredConstructor(parameterTypes).newInstance(inputObjects.toArray());
            } else {
                return aClass.getDeclaredConstructor().newInstance();
            }
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

    public void addBeanPostProfessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessorList.add(beanPostProcessor);
    }

    private Object initialize (Object object) {
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            object = beanPostProcessor.beforeInitialization(object);
            object = beanPostProcessor.afterInitialization(object);
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
        List<String> beanNamesForType = getBeanNamesForType(BeanPostProcessor.class);
        for (String beanName : beanNamesForType) {
            Object bean = getBean(beanName);
            beanPostProcessorList.add((BeanPostProcessor) bean);

        }
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
