package com.minispring2.demo.model;

/**
 * Bean 属性中的依赖引用，用于和普通值（如 String）区分。
 */
public class RuntimeBeanReference {

    private String beanName;

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public RuntimeBeanReference(String beanName) {
        this.beanName = beanName;
    }
}
