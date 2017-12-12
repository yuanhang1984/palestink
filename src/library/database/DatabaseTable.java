package library.database;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DatabaseMetaData;

public class DatabaseTable {
        /*
         * 连接数据库驱动
         */
        private String driver;
        /*
         * 连接数据库url
         */
        private String url;
        /*
         * 连接数据库用户名
         */
        private String name;
        /*
         * 连接数据库密码
         */
        private String password;

        /**
         * 构造函数
         * 
         * @param driver 驱动类型
         * @param url 连接地址
         * @param name 连接数据库的用户名
         * @param password 连接数据库的密码
         */
        public DatabaseTable(String driver, String url, String name, String password) {
                this.driver = driver;
                this.url = url;
                this.name = name;
                this.password = password;
        }

        /**
         * 数据库连接测试
         * 
         * @return 连接成功返回true，连接失败返回false。
         * @throws Exception
         */
        public boolean connectTest() throws Exception {
                Class.forName(this.driver);
                Connection c = null;
                try {
                        c = DriverManager.getConnection(this.url, this.name, this.password);
                        if (null == c) {
                                return false;
                        }
                        return true;
                } finally {
                        c.close();
                }
        }

        /**
         * 获取数据库表的List
         *
         * @return 数据库表名的ArrayList<String>
         * @throws Exception
         */
        public ArrayList<String> getTableList() throws Exception {
                Class.forName(this.driver);
                Connection c = null;
                try {
                        c = DriverManager.getConnection(this.url, this.name, this.password);
                        if (null == c) {
                                return null;
                        }
                        DatabaseMetaData dmd = c.getMetaData();
                        if (null == dmd) {
                                return null;
                        }
                        ResultSet rs = dmd.getTables(null, null, null, new String[] { "TABLE" });
                        ArrayList<String> tableList = new ArrayList<String>();
                        while (rs.next()) {
                                tableList.add(rs.getString("TABLE_NAME"));
                        }
                        return tableList;
                } finally {
                        c.close();
                }
        }

        /**
         * 获取表字段对象的List
         * 
         * @param tableName 表名
         * @return TableField对象列表
         * @throws Exception
         */
        public ArrayList<TableField> getTableFieldList(String tableName) throws Exception {
                Class.forName(this.driver);
                Connection c = null;
                try {
                        c = DriverManager.getConnection(this.url, this.name, this.password);
                        if (null == c) {
                                return null;
                        }
                        DatabaseMetaData dmd = c.getMetaData();
                        if (null == dmd) {
                                return null;
                        }

                        ResultSet rs = dmd.getColumns(null, "%", tableName, "%");
                        ArrayList<TableField> fieldList = new ArrayList<TableField>();
                        while (rs.next()) {
                                String allowNull = null;
                                if (0 == rs.getInt("NULLABLE")) {
                                        allowNull = TableField.allow_null_false;
                                } else {
                                        allowNull = TableField.allow_null_true;
                                }
                                fieldList.add(new TableField(rs.getString("COLUMN_NAME"), rs.getString("TYPE_NAME"), rs.getInt("COLUMN_SIZE"), allowNull));
                        }
                        return fieldList;
                } finally {
                        c.close();
                }
        }
}