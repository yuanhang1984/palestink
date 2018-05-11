package module.file_storage.necessary;

import java.io.File;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import library.string.CharacterString;
import library.thread.Block;
import framework.sdk.Framework;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

@SuppressWarnings("serial")
public class Config extends HttpServlet {
        public static final String MODULE_NAME = "file_storage.Config";
        /*
         * 检查文件的时间周期(单位:分钟)
         */
        public static int FILE_CHECK_TIME_CYCLE = 10;
        /*
         * 临时文件的生命周期(单位:分钟)
         */
        public static int TEMPORARY_FILE_LIFE_CYCLE = 60;
        /*
         * 永久文件的生命周期(单位:年)
         */
        public static int PERMANENT_FILE_LIFE_CYCLE = 100;

        public Config() {
        }

        private void getInitConfig(String path) {
                try {
                        File file = new File(path);
                        SAXReader reader = new SAXReader();
                        Document doc = reader.read(file);
                        Element root = doc.getRootElement();
                        Element fileStorage = root.element("FileStorage");
                        Config.FILE_CHECK_TIME_CYCLE = Integer.parseInt(fileStorage.attributeValue("fileCheckTimeCycle"));
                        Config.TEMPORARY_FILE_LIFE_CYCLE = Integer.parseInt(fileStorage.attributeValue("temporaryFileLifeCycle"));
                        Config.PERMANENT_FILE_LIFE_CYCLE = Integer.parseInt(fileStorage.attributeValue("permanentFileLifeCycle"));
                } catch (Exception e) {
                        Framework.LOG.error(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                }
        }

        @Override
        public void init(ServletConfig config) throws ServletException {
                super.init(config);
                String path = config.getInitParameter("path");
                this.getInitConfig(path);
                /*
                 * Daemon在Listener中启动，而Config在Servlet中启动（晚于前者），为了避免Daemon中读取Config数据出现错误，需要在Daemon增加一个线程锁，并当Config初始化结束后解锁。
                 */
                Block.unlock(Daemon.LOCK, Daemon.CONDITION);
        }
}