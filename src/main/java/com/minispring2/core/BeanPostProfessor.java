package com.minispring2.core;

public interface BeanPostProfessor {

    default Object beforeInitialization(Object object) {
        return object;
    }

    default Object afterInitialization(Object object) {
        return object;
    }

}
