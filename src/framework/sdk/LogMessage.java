package framework.sdk;

/**
 * 便于对消息处理的扩展
 */
public abstract class LogMessage {
        public LogMessage() {
        }

        /**
         * 消息处理前调用的方法
         * @param moduleName 模块名称
         * @param message 消息内容
         * @return 消息内容（影响日志内容）
         */
        public abstract String before(String moduleName, String message);

        /**
         * 消息处理后调用的方法（不影响日志内容）
         * @param moduleName 模块名称
         * @param message 消息内容
         */
        public abstract void after(String moduleName, String message);
}
