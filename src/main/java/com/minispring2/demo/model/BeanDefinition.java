package com.minispring2.demo.model;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {

    private Class aClass;

    private List<PropertyValue> propertyValueList = new ArrayList<>();

    public static class PropertyValue {
        private String name;
        private Object object;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getObject() {
            return object;
        }

        public void setObject(Object object) {
            this.object = object;
        }
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }
}
