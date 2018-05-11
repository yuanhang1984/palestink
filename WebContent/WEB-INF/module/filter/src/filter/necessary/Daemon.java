package module.filter.necessary;

import java.util.List;
import java.util.Iterator;
import javax.servlet.ServletContext;
import framework.sdk.Framework;
import framework.sdk.config.HttpConfig;
import framework.sdk.spec.module.necessary.DaemonAction;
import library.string.CharacterString;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

class ExecuteThread extends Thread {
        public ExecuteThread() {
        }

        @Override
        public void run() {
                String path = Framework.PROJECT_REAL_PATH + "WEB-INF/module/filter/res/config.xml";
                SAXReader reader = new SAXReader();
                Document doc = null;
                try {
                        doc = reader.read(path);
                } catch (Exception e) {
                        Framework.LOG.error(Daemon.MODULE_NAME, "Read Filter Config File Error: " + System.getProperty("line.separator") + CharacterString.getExceptionStackTrace(e));
                }
                Element root = doc.getRootElement();
                Element httpHeader = root.element("HttpHeader");
                HttpConfig.EVERY_ORIGIN = Boolean.parseBoolean(httpHeader.attributeValue("everyOrigin"));
                List<?> setList = httpHeader.elements("Set");
                int size = setList.size();
                if (0 >= size) {
                        return;
                }
                HttpConfig.HTTP_HEADER = new String[size];
                Iterator<?> setIter = setList.iterator();
                int i = 0;
                while (setIter.hasNext()) {
                        Element set = (Element) setIter.next();
                        String key = set.attributeValue("key");
                        String value = set.attributeValue("value");
                        HttpConfig.HTTP_HEADER[i] = key + HttpConfig.HTTP_HEADER_SPLIT + value;
                        i++;
                }
        }
}

public class Daemon extends DaemonAction {
        public static final String MODULE_NAME = "filter.Daemon";

        public Daemon(ServletContext servletContext) {
                super(servletContext);
        }

        @Override
        public void run() {
                new ExecuteThread().start();
        }
}