import com.fasterxml.jackson.databind.ObjectMapper;
import com.minispring2.core.MiniApplicationContext;
import com.minispring2.demo.dao.UserDao;
import com.minispring2.web.core.*;
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
     * curl http://localhost:8080/hello
     * curl "http://localhost:8080/getUser?userId=12345"
     * curl.exe -X POST http://localhost:8080/addUser -H "Content-Type: application/json" -d '{\"name\":\"Jerry\",\"age\":19}'
     * @throws Exception
     */
    @Test
    public void testGetUser() throws Exception {
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

    /**
     * requestBody
     * @throws Exception
     */
    @Test
    public void testAddUser() throws Exception {
        MiniApplicationContext miniApplicationContext = new MiniApplicationContext();
        miniApplicationContext.basicPackages = new String[]{"com.minispring2.demo", "com.minispring2.core", "com.minispring2.web"};
        miniApplicationContext.refresh();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.init(miniApplicationContext.defaultBeanFactory);



        UserDao userDao = new UserDao();
        userDao.setName("Jerry");
        userDao.setAge(19);
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(userDao);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestBody = s;
        RequestMappingInfo requestMappingInfo = new RequestMappingInfo();
        requestMappingInfo.path = "/addUser";
        requestMappingInfo.method = RequestMethod.POST;
        httpRequest.requestMappingInfo = requestMappingInfo;
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);

        System.out.println(httpResponse.responseBody);
    }
}
