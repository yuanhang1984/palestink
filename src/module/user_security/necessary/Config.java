package module.user_security.necessary;

import java.io.File;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import framework.sdk.Framework;
import module.user_security.necessary.Config;

@SuppressWarnings("serial")
public class Config extends HttpServlet {
        public static final String MODULE_NAME = "user_security.Config";
        /*
         * 登录失败重试计数(单位:次)
         */
        public static int LOGIN_FAILED_RETRY_COUNT = 5;
        /*
         * 账户冻结时间(单位:分钟)
         */
        public static int ACCOUNT_FROZEN_TIME = 10;

        public Config() {
        }

        private void getInitConfig(String path) {
                try {
                        File file = new File(path);
                        SAXReader reader = new SAXReader();
                        Document doc = reader.read(file);
                        Element root = doc.getRootElement();
                        Element logion = root.element("LogIOn");
                        Config.LOGIN_FAILED_RETRY_COUNT = Integer.parseInt(logion.attributeValue("loginFailedRetryCount"));
                        Config.ACCOUNT_FROZEN_TIME = Integer.parseInt(logion.attributeValue("accountFrozenTime"));
                } catch (Exception e) {
                        Framework.LOG.error(Config.MODULE_NAME, e.toString());
                }
        }

        @Override
        public void init(ServletConfig config) throws ServletException {
                super.init(config);
                String path = config.getInitParameter("path");
                this.getInitConfig(path);
        }
}
