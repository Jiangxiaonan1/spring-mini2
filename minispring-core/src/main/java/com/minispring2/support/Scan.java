package com.minispring2.support;

import com.minispring2.annotation.AnnotationUtils;
import com.minispring2.annotation.Autowired;
import com.minispring2.annotation.Component;
import com.minispring2.demo.model.BeanDefinition;
import com.minispring2.demo.model.RuntimeBeanReference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @since 2026-09-10 13:56:22
 **/
public class Scan {
    private ClassLoader classLoader;
    private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    public Scan (ClassLoader classLoader, Map<String, BeanDefinition> beanDefinitionMap) {
        this.classLoader = classLoader;
        this.beanDefinitionMap = beanDefinitionMap;
    }
    public void scan(String packageName) {
        // 拿到 URL 之后，你才能判断是目录还是 jar，再列出里面的 .class，Class.forName，看有没有 @Controller。
        packageName = packageName.replace(".", "/");
        try {
            Enumeration<URL> resources = this.classLoader.getResources(packageName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                File dir = new File(url.toURI());
                for (File file : dir.listFiles()) {
                    if(file.isDirectory()) {
                        scan(packageName + "." + file.getName());
                    } else if(file.getName().endsWith(".class")) {
                        String simpleName = file.getName().substring(0, file.getName().length() - ".class".length());
                        Class<?> aClass = classLoader.loadClass(packageName.replace("/", ".") + "." + simpleName);

                        if(AnnotationUtils.hasMeta(aClass.getAnnotations(), Component.class) && !aClass.isInterface() && !aClass.isAnnotation() && !Modifier.isAbstract(aClass.getModifiers())) {
                            BeanDefinition beanDefinition = new BeanDefinition(aClass);
                            Field[] declaredFields = aClass.getDeclaredFields();
                            if(declaredFields != null) {
                                List<BeanDefinition.PropertyValue> propertyValueList = beanDefinition.getPropertyValueList();
                                for (Field field : declaredFields) {
                                    if(AnnotationUtils.hasMeta(field.getAnnotations(), Autowired.class)) {
                                        propertyValueList.add(new BeanDefinition.PropertyValue(field.getName(), new RuntimeBeanReference(field.getName(), field.getType())));
                                    }
                                }
                            }
                            String beanName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
                            beanDefinition.setBeanName(beanName);
                            beanDefinitionMap.put(beanName, beanDefinition);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
