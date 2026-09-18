package com.minispring2.core.beanPostProfessor;

public interface BeanPostProcessor {

    default Object beforeInitialization(Object object) {
        return object;
    }

    default Object afterInitialization(Object object) {
        return object;
    }

}
