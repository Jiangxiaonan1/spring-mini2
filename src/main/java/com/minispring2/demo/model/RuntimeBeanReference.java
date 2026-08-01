package com.minispring2.demo.model;

/**
 * BeanDefinition的属性，与基本数据类型区分
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
