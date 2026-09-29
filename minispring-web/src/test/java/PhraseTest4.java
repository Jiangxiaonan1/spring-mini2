import com.minispring2.core.MiniApplicationContext;
import com.minispring2.web.core.Dispatcher;
import com.minispring2.web.servlet.DispatcherServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 *
 * @since 2026-09-10 15:00:42
 **/
public class PhraseTest4 {

    /**
     * requestParam
     *
     * curl.exe http://localhost:8080/user/hello
     * curl.exe "http://localhost:8080/user/getUser?userId=12345"
     * curl.exe -X POST http://localhost:8080/user/addUser -H "Content-Type: application/json" -d '{\"name\":\"Jerry\",\"age\":19}'
     * @throws Exception
     */
    @Test
    public void testWithCurl() throws Exception {
        MiniApplicationContext miniApplicationContext = new MiniApplicationContext();
        miniApplicationContext.basicPackages = new String[]{"com.minispring2.demo", "com.minispring2.core", "com.minispring2.web"};
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
