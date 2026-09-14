package com.minispring2.demo.model;

import java.util.ArrayList;
import java.util.List;

public class BeanDefinition {

    private Class<?> aClass;

    private List<PropertyValue> propertyValueList = new ArrayList<>();

    private String beanName;

    public static class PropertyValue {
        private String fieldName;
        private Object fieldValue;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Object getFieldValue() {
            return fieldValue;
        }

        public void setFieldValue(Object fieldValue) {
            this.fieldValue = fieldValue;
        }

        public PropertyValue() {
        }

        public PropertyValue(String fieldName, Object fieldValue) {
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
        }
    }

    public Class<?> getaClass() {
        return aClass;
    }

    public void setaClass(Class<?> aClass) {
        this.aClass = aClass;
    }

    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public void setPropertyValueList(List<PropertyValue> propertyValueList) {
        this.propertyValueList = propertyValueList;
    }


    public BeanDefinition() {
    }

    public BeanDefinition(Class<?> clazz) {
        this.aClass = clazz;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
