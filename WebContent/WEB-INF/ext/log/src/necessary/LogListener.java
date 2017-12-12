package ext.log.necessary;

import framework.sdk.LogMessage;

public class LogListener extends LogMessage {
        /**
         * 消息处理前调用的方法
         * @param moduleName 模块名称
         * @param message 消息内容
         * @return 消息内容（影响日志内容）
         */
        @Override
        public String before(String moduleName, String message) {
                return message;
        }

        /**
         * 消息处理后调用的方法（不影响日志内容）
         * @param moduleName 模块名称
         * @param message 消息内容
         */
        @Override
        public void after(String moduleName, String message) {
        }
}