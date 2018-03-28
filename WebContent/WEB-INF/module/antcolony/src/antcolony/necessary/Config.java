package module.antcolony.necessary;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import module.antcolony.necessary.Config;

@SuppressWarnings("serial")
public class Config extends HttpServlet {
        public static final String MODULE_NAME = "antcolony.Config";

        public Config() {
        }

        @Override
        public void init(ServletConfig config) throws ServletException {
                super.init(config);
        }
}
