package framework.core;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import framework.sdk.msg.Message;
import library.io.InputOutput;
import library.string.CharacterString;

@SuppressWarnings("serial")
public class ModuleDocs extends HttpServlet {
        private String path;

        public ModuleDocs() {
        }

        /**
         * 获取模块的api
         */
        private String getModuleDocs() throws Exception {
                StringBuilder sb = InputOutput.simpleStringBuilderReadFile(path);
                if (null == sb) {
                        return null;
                }
                return sb.toString();
        }

        /**
         * 初始化<br />
         * 从参数读取配置文件信息
         */
        @Override
        public void init() throws ServletException {
                super.init();
                this.path = this.getInitParameter("path");
        }

        /**
         * doGet
         */
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) {
                doPost(request, response);
        }

        /**
         * doPost
         */
        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) {
                try {
                        String s = this.getModuleDocs();
                        if (s != null) {
                                Message.responseToClient(response, s);
                        } else {
                                Message.send(request, response, Message.STATUS.ERROR, Message.ERROR.OTHER, null, "Module Document Read Error: " + System.getProperty("line.separator") + path);
                        }
                } catch (Exception e) {
                        Message.send(request, response, Message.STATUS.EXCEPTION, Message.ERROR.OTHER, null, CharacterString.getExceptionStackTrace(e));
                }
        }
}