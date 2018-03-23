package module.user_security.necessary;

import java.util.HashMap;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.CustomAction;
import module.user_security.optional.LogIOn;
import module.user_security.optional.RolePermission;

public class Custom extends CustomAction {
        private HttpServlet httpServlet;
        private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;
        private HashMap<String, Object> parameter;
        private Connection connection;
        private LogIOn logion;
        private RolePermission rolePermission;

        public Custom(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                this.httpServlet = httpServlet;
                this.httpServletRequest = httpServletRequest;
                this.httpServletResponse = httpServletResponse;
                this.parameter = parameter;
                this.connection = connection;
                this.logion = new LogIOn(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.connection, this.parameter);
                this.rolePermission = new RolePermission(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.connection, this.parameter);
        }

        public Message login() {
                return this.logion.login();
        }

        public Message logout() {
                return this.logion.logout();
        }

        public Message getAllPermission() {
                return this.rolePermission.getAllPermission();
        }
}
