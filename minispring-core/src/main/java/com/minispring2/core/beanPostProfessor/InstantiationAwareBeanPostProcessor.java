package com.minispring2.core.beanPostProfessor;

/**
 * @description
 * @create 2026-09-17 17:48:15
 **/
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor{

    default void postProcess(Object bean) {}

}
