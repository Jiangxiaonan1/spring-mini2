package com.minispring2.support;

import com.minispring2.annotation.Controller;
import com.minispring2.demo.model.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
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
                        scan(file.getPath());
                    } else if(file.getName().endsWith(".class")) {
                        Class<?> aClass = classLoader.loadClass(file.getPath().replace(".", "/"));
                        beanDefinitionMap.put(file.getName(), new BeanDefinition(aClass));
                        if(aClass.isAnnotationPresent(Controller.class)) {

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
