package com.minispring2.core;

/**
 *
 * @since 2026-09-11 14:08:37
 **/
public class MiniApplicationContext {
    String basicPackage;
    public DefaultBeanFactory defaultBeanFactory;
    public MiniApplicationContext(String basicPackage) {
        this.basicPackage = basicPackage;
    }
    public void refresh() {
        /**
         *   1. 扫描指定包
         *   2. 注册 BeanDefinition
         *   3. 注册 BeanPostProcessor
         *   4. 预实例化非懒加载单例
         *   5. 标记容器启动完成
         */
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        this.defaultBeanFactory = defaultBeanFactory;
        defaultBeanFactory.scan(basicPackage);
        defaultBeanFactory.preInstantiateSingletons();
    }
}
