package library.system;

public class SystemKit {
        /*
         * 系统名称（小写）
         */
        private static String OS_NAME = System.getProperty("os.name").toLowerCase();

        /**
         * 判断是否为windows系统
         * @return 如果是windows系统返回true，如果不是返回false。
         */
        public static boolean isWindows() {
                if (-1 != OS_NAME.indexOf("windows")) {
                        return true;
                }
                return false;
        }
}