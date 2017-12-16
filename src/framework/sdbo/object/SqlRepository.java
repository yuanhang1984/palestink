package framework.sdbo.object;

import java.util.HashMap;
import org.dom4j.Element;

public class SqlRepository {
        /*
         * 因为HashMap的key区分大小写，为了避免发生因编码错误而导致的数据错误，因此这里做了一个封装。
         */
        private static HashMap<String, Element> MODULE_SQL = new HashMap<String, Element>();

        public static Element get(String key) {
                return MODULE_SQL.get(key.toLowerCase());
        }

        public static void put(String key, Element element) {
                MODULE_SQL.put(key.toLowerCase(), element);
        }
}