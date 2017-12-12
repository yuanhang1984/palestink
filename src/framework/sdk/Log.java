package framework.sdk;

/**
 * 日志的抽象类
 */
public abstract class Log {
        /**
         * 日志对象初始化
         */
        public abstract boolean init();

        /**
         * 日志对象释放资源
         */
        public abstract void releaseLogResource();

        /**
         * 日志消息输出（debug级别）
         * 
         * @param msg 消息内容
         */
        public abstract void debug(String moduleName, String msg);

        /**
         * 日志消息输出（info级别）
         * 
         * @param msg 消息内容
         */
        public abstract void info(String moduleName, String msg);

        /**
         * 日志消息输出（warn级别）
         * 
         * @param msg 消息内容
         */
        public abstract void warn(String moduleName, String msg);

        /**
         * 日志消息输出（error级别）
         * 
         * @param msg 消息内容
         */
        public abstract void error(String moduleName, String msg);

        /**
         * 日志消息输出（fatal级别）
         * 
         * @param msg 消息内容
         */
        public abstract void fatal(String moduleName, String msg);
}