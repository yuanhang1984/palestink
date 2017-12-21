package module.demo.necessary;

import java.sql.Connection;
import javax.servlet.ServletContext;
import framework.sdk.Framework;
import framework.sdk.SqlHandle;
import framework.sdk.DaemonAction;
import framework.sdk.DbInstanceModel;

class ExecuteThread extends Thread {
        private SqlHandle sqlHandler;

        public ExecuteThread(SqlHandle sqlHandler) {
                this.sqlHandler = sqlHandler;
        }

        @Override
        public void run() {
                for (;;) {
                        // 这里的数据库操作没有意义，只是说明如何使用数据库的操作。
                        DbInstanceModel dim = this.sqlHandler.getSqlModel(true);
                        Connection c = dim.getConnection();
                        try {
                                Framework.LOG.info(Daemon.MODULE_NAME, "AuthorInfo Name: " + Config.AUTHORINFO_OBJECT.getName());
                                Framework.LOG.info(Daemon.MODULE_NAME, "AuthorInfo Birthday: " + Config.AUTHORINFO_OBJECT.getBirthday());
                                break;
                        } catch (Exception e) {
                                Framework.LOG.error(Daemon.MODULE_NAME, e.toString());
                        } finally {
                                try {
                                        c.close();
                                } catch (Exception e) {
                                        Framework.LOG.warn(Daemon.MODULE_NAME, e.toString());
                                }
                        }
                }
                Framework.LOG.info(Daemon.MODULE_NAME, "Thread Exit");
        }
}

public class Daemon extends DaemonAction {
        public static final String MODULE_NAME = "Demo.Daemon";
        private SqlHandle sqlHandler;

        public Daemon(ServletContext servletContext, SqlHandle sqlHandler) {
                super(servletContext, sqlHandler);
                this.sqlHandler = sqlHandler;
        }

        @Override
        public void run() {
                new ExecuteThread(this.sqlHandler).start();
        }
}