import com.minispring2.core.DefaultBeanFactory;
import com.minispring2.web.core.Dispatcher;
import com.minispring2.web.core.HttpRequest;
import com.minispring2.web.core.HttpResponse;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

/**
 *
 * @since 2026-09-10 15:00:42
 **/
public class PhraseTest2 {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException {
        DefaultBeanFactory defaultBeanFactory = new DefaultBeanFactory();
        defaultBeanFactory.scan("com.minispring2.web");

        Dispatcher dispatcher = new Dispatcher();

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestUrl = "hello";
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);

        System.out.println(httpResponse.responseBody);
    }
}
