import com.fasterxml.jackson.databind.ObjectMapper;
import com.minispring2.core.MiniApplicationContext;
import com.minispring2.demo.dao.UserDao;
import com.minispring2.web.core.Dispatcher;
import com.minispring2.web.core.HttpRequest;
import com.minispring2.web.core.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 *
 * @since 2026-09-10 15:00:42
 **/
public class PhraseTest3 {

    /**
     * requestParam
     * @throws Exception
     */
    @Test
    public void testGetUser() throws Exception {
        MiniApplicationContext miniApplicationContext = new MiniApplicationContext();
        miniApplicationContext.basicPackages = new String[]{"com.minispring2.demo", "com.minispring2.core", "com.minispring2.web"};
        miniApplicationContext.refresh();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.init(miniApplicationContext.defaultBeanFactory);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestUrl = "/getUser";
        httpRequest.requestParams = new HashMap<String, Object>(){{put("userId", 12345L);}};
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);

        System.out.println(httpResponse.responseBody);
    }

    /**
     * requestParam
     * String -> Integer
     * @throws Exception
     */
    @Test
    public void testGetUser2() throws Exception {
        MiniApplicationContext miniApplicationContext = new MiniApplicationContext();
        miniApplicationContext.basicPackages = new String[]{"com.minispring2.demo", "com.minispring2.core", "com.minispring2.web"};
        miniApplicationContext.refresh();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.init(miniApplicationContext.defaultBeanFactory);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestUrl = "/getUser";
        httpRequest.requestParams = new HashMap<String, Object>(){{put("userId", "12345");}};
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);

        System.out.println(httpResponse.responseBody);
    }

    /**
     * requestBody
     * @throws Exception
     */
    @Test
    public void testGetUser3() throws Exception {
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
        httpRequest.requestUrl = "/addUser";
        httpRequest.requestBody = s;
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);

        System.out.println(httpResponse.responseBody);
    }
}
