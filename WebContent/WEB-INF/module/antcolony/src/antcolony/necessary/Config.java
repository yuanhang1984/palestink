package module.antcolony.necessary;

import java.io.File;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import framework.sdk.Framework;
import module.antcolony.necessary.Config;

@SuppressWarnings("serial")
public class Config extends HttpServlet {
        public static final String MODULE_NAME = "antcolony.Config";

        /*
         * 启动WebServer的命令(如果内容为空, 则不启用该功能)
         */
        public static String START_COMMAND = "";

        /*
         * 停止WebServer的命令(如果内容为空, 则不启用该功能)
         */
        public static String STOP_COMMAND = "";

        /*
         * 日志文件的路径(用于读取显示在前端)(如果内容为空, 则不启用该功能)
         */
        public static String LOG_FILE_PATH = "";

        public Config() {
        }

        private void getInitConfig(String path) {
                try {
                        File file = new File(path);
                        SAXReader reader = new SAXReader();
                        Document doc = reader.read(file);
                        Element root = doc.getRootElement();
                        Element webServer = root.element("WebServer");
                        Config.START_COMMAND = webServer.attributeValue("startCommand");
                        Config.STOP_COMMAND = webServer.attributeValue("stopCommand");
                        Config.LOG_FILE_PATH = webServer.attributeValue("logFilePath");
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
