package framework.sdk;

import java.util.ArrayList;
import framework.sdk.log.Log;

public class Framework {
        /*
         * 项目真实路径
         */
        public static String PROJECT_REAL_PATH = "";

        /*
         * 模块名称的列表
         */
        public static ArrayList<String> MODULE_NAME_LIST;

        /*
         * 声明全局日志对象
         */
        public static Log LOG = null;

        /*
         * 账号角色的session编号
         */
        public static final String USER_UUID = "user_uuid";

        /*
         * 账号角色的session名称
         */
        public static final String USER_ROLE = "user_role";

        /*
         * 日志源码编译选项
         * 0: 不编译.
         * 1: 编译.
         */
        public static int LOG_SOURCE_CODE_REBUILD = 0;

        /*
         * 编译源码的命令
         */
        public static String LOG_SOURCE_CODE_COMMAND = "";

        /*
         * 日志文件是否开启
         * true: 启用日志文件.
         * false: 禁用日志文件.
         */
        public static boolean LOG_FILE_ENABLE = true;

        /*
         * 日志文件存放路径
         */
        public static String LOG_FILE_LOG_PATH = "";

        /*
         * 日志压缩文件存放路径
         */
        public static String LOG_FILE_ZIP_PATH = "";

        /*
         * 日志文件尺寸(MB), 超出后将打包成zip.
         */
        public static int LOG_FILE_SIZE = 10;

        /*
         * 数据库源码编译选项
         * 0: 不编译.
         * 1: 编译.
         */
        public static int DB_SOURCE_CODE_REBUILD = 0;

        /*
         * 编译源码的命令
         */
        public static String DB_SOURCE_CODE_COMMAND = "";

        /*
         * 日志消息格式
         */
        public static String LOG_FILE_FORMAT = "";

        /*
         * 数据库用户名
         */
        public static String DB_SECURITY_NAME = "";
        /*
         * 数据库密码
         */
        public static String DB_SECURITY_PASSWORD = "";
        /*
         * 数据库驱动
         */
        public static String DB_INFO_DRIVER = "";
        /*
         * 数据库URL
         */
        public static String DB_INFO_URL = "";
        /*
         * 数据库连接池最大连接数
         */
        public static int DB_POOL_MAXACTIVECONNECTION = 0;
}