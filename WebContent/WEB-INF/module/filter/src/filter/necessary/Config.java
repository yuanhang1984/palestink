package module.filter.necessary;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

@SuppressWarnings("serial")
public class Config extends HttpServlet {
        public static final String MODULE_NAME = "filter.Config";

        public Config() {
        }

        @Override
        public void init(ServletConfig config) throws ServletException {
                super.init(config);
        }
}