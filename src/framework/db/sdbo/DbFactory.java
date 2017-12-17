package framework.db.sdbo;

import java.util.HashMap;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import framework.sdk.DbInstanceModel;
import framework.sdk.Framework;
import ext.db.necessary.DbInstance;

public class DbFactory {
        private static final String MODULE_NAME = "DbFactory";

        private DbFactory() {
        }

        private static class InstanceMaker {
                private static final DbInstanceModel INSTANCE = new DbInstance();
        }

        public static final DbInstanceModel getInstance() {
                return InstanceMaker.INSTANCE;
        }

        public static boolean init(String driver, String url, String name, String password, int maxActiveConnection) {
                return DbFactory.getInstance().init(driver, url, name, password, maxActiveConnection);
        }

        public static Connection getConnection() {
                return DbFactory.getInstance().getConnection();
        }

        public static void releaseResource() {
                DbFactory.getInstance().release();
        }

        /**
         * idu执行（用于insert、delete、update三种操作）
         * @param sql sql语句
         * @return 执行sql影响的行数，发生异常返回-1。
         */
        public static int iduExecute(Connection con, String sql) {
                PreparedStatement ps = null;
                try {
                        ps = con.prepareStatement(sql);
                        return ps.executeUpdate();
                } catch (Exception e) {
                        Framework.LOG.error(DbFactory.MODULE_NAME, e.toString());
                        return -1;
                } finally {
                        try {
                                if (null != ps) {
                                        ps.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.error(DbFactory.MODULE_NAME, e.toString());
                                return -1;
                        }
                }
        }

        public static ArrayList<HashMap<String, Object>> select(Connection con, String sql) {
                ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
                PreparedStatement ps = null;
                ResultSet rs = null;
                ResultSetMetaData rsmd = null;
                try {
                        ps = con.prepareStatement(sql);
                        rs = ps.executeQuery();
                        rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
                        String[] columnName = new String[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                                columnName[i] = rsmd.getColumnName(i + 1);
                        }
                        while (rs.next()) {
                                HashMap<String, Object> data = new HashMap<String, Object>();
                                for (int i = 0; i < columnName.length; i++) {
                                        data.put(columnName[i], rs.getObject(columnName[i]));
                                }
                                result.add(data);
                        }
                        return result;
                } catch (Exception e) {
                        Framework.LOG.error(DbFactory.MODULE_NAME, e.toString());
                        return null;
                } finally {
                        try {
                                if (null != rs) {
                                        rs.close();
                                }
                                if (null != ps) {
                                        ps.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.error(DbFactory.MODULE_NAME, e.toString());
                                return null;
                        }
                }
        }
}