package framework.sdk.spec.module.necessary;

import java.util.HashMap;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CustomAction {
        public CustomAction(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
        }
}