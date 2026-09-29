import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @since 2026-09-29 09:04:57
 **/
public class PatternTest {

    @Test
    public void test() {
        String s = "user/getUser/{id}";
        Pattern p = Pattern.compile("user/getUser/\\{[^{}]*\\}");   // 正则本体是 \{...\}
        Matcher m = p.matcher(s);
        while (m.find()) {
            System.out.println(m.group());   // 输出 { int x = 1; }
        }
    }

    @Test
    public void test2() {
        String s = "user/getUser/12345";
        Pattern p = Pattern.compile("user/getUser/([^/]+)");
        Matcher m = p.matcher(s);
        while (m.find()) {
            System.out.println(m.group(1));
        }
    }

    /**
     *
     * 将字符串中的{id}替换为\{.*\}
     */
    @Test
    public void test3() {
        String s = "user/getUser/{id}";
        String s1 = s.replaceFirst("\\{.*\\}", "(.*)");
        System.out.println(s);
        System.out.println(s1); // user/getUser/\{.*\}
    }

    /**
     *
     * 使用正则提取路径参数
     */
    @Test
    public void test4() {
        String s = "http://localhost:8080/user/getUserByPath/12345";
        Pattern p = Pattern.compile("/user/getUserByPath/(.*)");
        Matcher m = p.matcher(s);
        String destinationString = "";
        while (m.find()) {
            destinationString = m.group(1);
            System.out.println(destinationString);
            break;
        }
    }

    /**
     *
     * 判断是否有{}
     */
    @Test
    public void test5() {
        String s = "user/getUser/{id}";
        Pattern p = Pattern.compile(".*\\{[a-zA-Z]*\\}.*");
        Matcher m = p.matcher(s);
        while (m.find()) {
            System.out.println(m.group());
        }
    }
}
