package ext.db.necessary;

import java.util.HashMap;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.ResultSetMetaData;
import java.sql.PreparedStatement;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import framework.sdk.Log;
import framework.sdk.SqlModel;
import framework.sdk.Framework;
import library.database.DatabaseKit;

public class DbFactory extends SqlModel {
        private PoolProperties poolProperties;
        private DataSource dataSource;

        public DbFactory() {
                this.poolProperties = new PoolProperties();
                this.dataSource = new DataSource();
        }

        @Override
        public boolean init(Log LOG, String driver, String url, String name, String password, int maxActiveConnection) {
                try {
                        // 设置连接池的基本属性（来自init参数）
                        this.poolProperties.setDriverClassName(driver);
                        this.poolProperties.setUrl(url);
                        this.poolProperties.setUsername(name);
                        this.poolProperties.setPassword(password);
                        this.poolProperties.setMaxActive(maxActiveConnection);
                        // 设置连接池属性至数据源
                        this.dataSource.setPoolProperties(this.poolProperties);
                } catch (Exception e) {
                        Framework.LOG.error(SqlModel.MODULE_NAME, "Initialize DbFactory Error: " + e.toString());
                        return false;
                }
                return true;
        }

        @Override
        public Connection getConnection() {
                try {
                        return this.dataSource.getConnection();
                } catch (Exception e) {
                        Framework.LOG.error(SqlModel.MODULE_NAME, e.toString());
                        return null;
                }
        }

        /**
         * idu执行（用于insert、delete、update三种操作）
         * @param sql sql语句
         * @param parameter 参数
         * @return 执行sql影响的行数，发生异常返回-1。
         */
        public int iduExecute(Connection con, String sql, HashMap<String, Object> parameter) {
                String completeSql = DatabaseKit.generateSql(sql, parameter);
                PreparedStatement ps = null;
                try {
                        ps = con.prepareStatement(completeSql);
                        return ps.executeUpdate();
                } catch (Exception e) {
                        Framework.LOG.error(SqlModel.MODULE_NAME, e.toString());
                        return -1;
                } finally {
                        try {
                                if (null != ps) {
                                        ps.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.error(SqlModel.MODULE_NAME, e.toString());
                                return -1;
                        }
                }
        }

        @Override
        public int insert(Connection con, String sql, HashMap<String, Object> parameter) {
                return this.iduExecute(con, sql, parameter);
        }

        @Override
        public int delete(Connection con, String sql, HashMap<String, Object> parameter) {
                return this.iduExecute(con, sql, parameter);
        }

        @Override
        public int update(Connection con, String sql, HashMap<String, Object> parameter) {
                return this.iduExecute(con, sql, parameter);
        }

        @Override
        public ArrayList<HashMap<String, Object>> select(Connection con, String sql, HashMap<String, Object> parameter) {
                ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
                String completeSql = DatabaseKit.generateSql(sql, parameter);
                PreparedStatement ps = null;
                ResultSet rs = null;
                ResultSetMetaData rsmd = null;
                try {
                        ps = con.prepareStatement(completeSql);
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
                        Framework.LOG.error(SqlModel.MODULE_NAME, e.toString());
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
                                Framework.LOG.error(SqlModel.MODULE_NAME, e.toString());
                                return null;
                        }
                }
        }

        @Override
        public void release() {
                this.dataSource.close();
        }
}