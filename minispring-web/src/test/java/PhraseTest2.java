import com.minispring2.core.MiniApplicationContext;
import com.minispring2.web.core.Dispatcher;
import com.minispring2.web.core.HttpRequest;
import com.minispring2.web.core.HttpResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @since 2026-09-10 15:00:42
 **/
public class PhraseTest2 {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException {
        MiniApplicationContext miniApplicationContext = new MiniApplicationContext();
        miniApplicationContext.basicPackages = new String[]{"com.minispring2.demo", "com.minispring2.core", "com.minispring2.web"};
        miniApplicationContext.refresh();

        Dispatcher dispatcher = new Dispatcher();
        dispatcher.init(miniApplicationContext.defaultBeanFactory);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestUrl = "/hello";
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);

        System.out.println(httpResponse.responseBody);
        assertEquals("hello world", httpResponse.responseBody);
    }
}
