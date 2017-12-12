package framework.sdk;

import java.util.HashMap;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CustomAction {
        public CustomAction(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, SqlHandle dbHandler, HashMap<String, Object> parameter) {
        }

        public CustomAction(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, SqlModel sqlSession, HashMap<String, Object> parameter) {
        }
}