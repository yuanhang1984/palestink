package module.filter.necessary;

import java.util.HashMap;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import framework.sdk.spec.module.necessary.CustomAction;

public class Custom extends CustomAction {
        public static final String MODULE_NAME = "filter.Custom";

        public Custom(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
        }
}