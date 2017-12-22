package module.demo.necessary;

import java.io.File;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import framework.sdk.Framework;
import module.demo.optional.AuthorInfo;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

@SuppressWarnings("serial")
public class Config extends HttpServlet {
        public static final String MODULE_NAME = "demo.Config";

        public static AuthorInfo AUTHORINFO_OBJECT = new AuthorInfo();

        public Config() {
        }

        private void getInitConfig(String path) {
                try {
                        File file = new File(path);
                        SAXReader reader = new SAXReader();
                        Document doc = reader.read(file);
                        Element root = doc.getRootElement();
                        Element authorInfo = root.element("AuthorInfo");
                        Config.AUTHORINFO_OBJECT.setName(authorInfo.attributeValue("name"));
                        Config.AUTHORINFO_OBJECT.setBirthday(authorInfo.attributeValue("birthday"));
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