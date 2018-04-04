package module.antcolony.necessary;

import java.util.HashMap;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.CustomAction;
import module.antcolony.optional.Module;

public class Custom extends CustomAction {
        private HttpServlet httpServlet;
        private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;
        private HashMap<String, Object> parameter;
        private Connection connection;
        private Module module;

        public Custom(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                this.httpServlet = httpServlet;
                this.httpServletRequest = httpServletRequest;
                this.httpServletResponse = httpServletResponse;
                this.parameter = parameter;
                this.connection = connection;
                this.module = new Module(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.connection, this.parameter);
        }

        public Message getLoadNameList() {
                return this.module.getLoadNameList();
        }

        public Message getDiskNameList() {
                return this.module.getDiskNameList();
        }

        public Message getPermissionList() {
                return this.module.getPermissionList();
        }

        public Message readServerResourceFile() {
                return this.module.readServerResourceFile();
        }

        public Message downloadServerResourceFile() {
                return this.module.downloadServerResourceFile();
        }

        public Message uploadServerResourceFile() {
                return this.module.uploadServerResourceFile();
        }

        public Message createModule() {
                return this.module.createModule();
        }

        public Message removeModule() {
                return this.module.removeModule();
        }

        public Message modifyModuleName() {
                return this.module.modifyModuleName();
        }
}