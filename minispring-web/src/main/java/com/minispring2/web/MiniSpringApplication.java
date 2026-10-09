package com.minispring2.web;

import com.minispring2.core.MiniApplicationContext;
import com.minispring2.web.core.Dispatcher;
import com.minispring2.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 *
 * @since 2026-10-09 10:05:23
 **/
public class MiniSpringApplication {

    public static void main(String[] args) throws Exception {
        MiniApplicationContext miniApplicationContext = new MiniApplicationContext();
        miniApplicationContext.basicPackages = new String[]{
                "com.minispring2.demo", "com.minispring2.core", "com.minispring2.web"
        };
        miniApplicationContext.refresh();
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.init(miniApplicationContext.defaultBeanFactory);
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();
        Context context = tomcat.addContext("", new File(".").getAbsolutePath());
        DispatcherServlet dispatcherServlet = new DispatcherServlet(dispatcher);
        Tomcat.addServlet(context, "dispatcherServlet", dispatcherServlet);
        context.addServletMappingDecoded("/*", "dispatcherServlet");
        tomcat.start();
        tomcat.getServer().await();
    }

}
