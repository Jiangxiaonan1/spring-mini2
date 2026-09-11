package com.minispring2.annotation;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @since 2026-09-11 10:14:45
 **/
public class AnnotationUtils {
    public static boolean hasMeta(Class<? extends Annotation> aClass, Class<? extends Annotation> destinationMeta, Set<Class> visited) {
        if(! visited.add(aClass)) {
            return false;
        }
        if (aClass.equals(destinationMeta)) {
            return true;
        }
        if(aClass.isAnnotationPresent(destinationMeta)) {
            return true;
        }
        Annotation[] annotations = aClass.getAnnotations();
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                Class<? extends Annotation> aClass1 = annotation.annotationType();
                if(aClass1.getName().startsWith("java.lang.annotation")) {
                   continue;
                }
                if(hasMeta(aClass1, destinationMeta, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean hasMeta(Annotation[] annotations, Class<? extends Annotation> destinationMeta) {
        if (annotations != null) {
            for (Annotation annotation : annotations) {
                if(annotation.equals(destinationMeta)) {
                    return true;
                }
                Class<? extends Annotation> aClass1 = annotation.annotationType();
                if(aClass1.getName().startsWith("java.lang.annotation")) {
                    continue;
                }
                if(hasMeta(aClass1, destinationMeta, new HashSet<>())) {
                    return true;
                }
            }
        }

        return false;
    }


}
