package ext.db.necessary;

import java.sql.Connection;
import framework.sdk.DbModel;
import framework.sdk.Framework;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

public class DbInstance extends DbModel {
        private PoolProperties poolProperties;
        private DataSource dataSource;
        private final String moduleName = "DbInstance";

        public DbInstance() {
                this.poolProperties = new PoolProperties();
                this.dataSource = new DataSource();
        }

        @Override
        public boolean init(String driver, String url, String name, String password, int maxActiveConnection) {
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
                        Framework.LOG.error(this.moduleName, "Initialize DbInstance Error: " + e.toString());
                        return false;
                }
                return true;
        }

        @Override
        public Connection getConnection() {
                try {
                        return this.dataSource.getConnection();
                } catch (Exception e) {
                        Framework.LOG.error(this.moduleName, e.toString());
                        return null;
                }
        }

        @Override
        public void release() {
                this.dataSource.close();
        }
}