import controller.UserController;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @since 2026-09-09 17:16:48
 **/
public class PhraseTest1 {

    @Test
    public void test() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Dispatcher dispatcher = new Dispatcher();
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.methodName = UserController.class.getMethod("hello");
        requestMapping.object = UserController.class.newInstance();
        dispatcher.requestMappingMap.put("hello", requestMapping);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.requestUrl = "hello";
        HttpResponse httpResponse = new HttpResponse();
        dispatcher.doService(httpRequest, httpResponse);

        System.out.println(httpResponse.responseBody);
    }

}
