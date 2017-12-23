package framework.sdk;

public class HttpConfig {
        /*
         * 编码方式
         */
        public static String REQUEST_CHARACTER_ENCODING = "utf-8";
        public static String RESPONSE_CHARACTER_ENCODING = "utf-8";
        public static String RESPONSE_CONTENT_TYPE_ENCODING = "application/json";

        /*
         * header数组
         */
        public static String[] HTTP_HEADER = null;

        /*
         * header数据分隔字符串
         */
        public static String HTTP_HEADER_SPLIT = "HttpConfigHttpHeaderSplit";

        /*
         * 是否设置为所有来源（动态添加）
         */
        public static boolean EVERY_ORIGIN = false;
}
