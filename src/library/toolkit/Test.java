package library.toolkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {
        public Test() {
                String s = "http://127.0.0.1:8080/palestink/module/demo/selectEmployeeInfo&employee?Uuid=25b32cc252f14059a481d335b71bc8d0&offset=0&rows=10";
                Pattern pattern = Pattern.compile("(.+?)\\/");
                Matcher matcher = pattern.matcher(s);
                if (!matcher.find()) {
                        // 如果没有找到需要替换的变量，直接返回sql语句的文本。
                        System.out.println("error");
                }
                // 上面find了一次，这里需要重置一下。
                matcher.reset();
                // 循环遍历语句中的每个变量
                while (matcher.find()) {
                        System.out.println(matcher.group(1));
                }
        }

        public static void main(String[] args) {
                new Test();
        }
}