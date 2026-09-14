package com.minispring2.demo.model;

/**
 * Bean 属性中的依赖引用，用于和普通值（如 String）区分。
 */
public class RuntimeBeanReference {

    private String beanName;

    private Class aClass;

    public String getBeanName() {
        return beanName;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public RuntimeBeanReference(String beanName) {
        this.beanName = beanName;
    }

    public RuntimeBeanReference(String beanName, Class aClass) {
        this.aClass = aClass;
        this.beanName = beanName;
    }
}
