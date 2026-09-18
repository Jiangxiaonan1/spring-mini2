package com.minispring2.core.beanPostProfessor;

import com.minispring2.annotation.AnnotationUtils;
import com.minispring2.annotation.Autowired;
import com.minispring2.annotation.Component;
import com.minispring2.core.DefaultBeanFactory;

import java.lang.reflect.Field;

/**
 *
 * @since 2026-09-14 16:42:41
 **/
@Component
public class AutowiredAnnotationBeanPostProcessor extends DefaultBeanFactory implements InstantiationAwareBeanPostProcessor {
    @Override
    public void injectAutowiredFields(Object bean) {

        Field[] declaredFields = bean.getClass().getDeclaredFields();
        if(declaredFields != null) {

            for (Field field : bean.getClass().getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if(field.get(bean) == null && AnnotationUtils.hasMeta(field.getAnnotations(), Autowired.class)) {
                        field.set(bean, getBeanForType(field.getType()));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}
