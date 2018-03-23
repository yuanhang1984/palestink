package module.user_security.necessary;

import java.util.HashMap;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletContext;
import org.dom4j.Element;
import library.database.DatabaseKit;
import framework.sdk.Framework;
import framework.ext.factory.DbFactory;
import framework.sdbo.object.SqlRepository;
import framework.sdk.spec.module.necessary.DaemonAction;

class ExecuteThread extends Thread {
        public ExecuteThread() {
        }

        @Override
        public void run() {
                Connection c = DbFactory.getConnection();
                if (null == c) {
                        Framework.LOG.error(Daemon.MODULE_NAME, "Get Database Connection Error");
                        return;
                }
                HashMap<String, Object> p = null;
                String sql = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                Element sqlRoot = SqlRepository.get("user_security");
                if (null == sqlRoot) {
                        Framework.LOG.error(Daemon.MODULE_NAME, "Read Sql Config File Error");
                        return;
                }
                try {
                        p = new HashMap<String, Object>();
                        sql = DatabaseKit.composeSql(sqlRoot, "selectRole", p);
                        if (0 >= sql.trim().length()) {
                                Framework.LOG.error(Daemon.MODULE_NAME, "Compose Sql Error");
                                return;
                        }
                        ps = c.prepareStatement(sql);
                        rs = ps.executeQuery();
                        while (rs.next()) {
                                String name = rs.getString("name");
                                String permissionList = rs.getString("permission_list");
                                Framework.USER_ROLE_MAP.put(name, permissionList);
                        }
                        Framework.LOG.info(Daemon.MODULE_NAME, "User Role Load Complete");
                } catch (Exception e) {
                        Framework.LOG.error(Daemon.MODULE_NAME, "Get User Role Error: " + System.getProperty("line.separator") + e.toString());
                } finally {
                        try {
                                if (null != rs) {
                                        rs.close();
                                }
                                if (null != ps) {
                                        ps.close();
                                }
                                if (null != c) {
                                        c.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.error(Daemon.MODULE_NAME, "Get User Role Error: " + System.getProperty("line.separator") + e.toString());
                        }
                }
        }
}

public class Daemon extends DaemonAction {
        public static final String MODULE_NAME = "user_security.Daemon";

        public Daemon(ServletContext servletContext) {
                super(servletContext);
        }

        @Override
        public void run() {
                new ExecuteThread().start();
        }
}
