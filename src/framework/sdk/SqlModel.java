package framework.sdk;

import java.util.HashMap;
import java.sql.Connection;
import java.util.ArrayList;

public abstract class SqlModel {
        public static final String MODULE_NAME = "SqlModule";

        public abstract boolean init(Log LOG, String driver, String url, String name, String password, int maxActiveConnection);

        public abstract Connection getConnection();

        public abstract int insert(Connection con, String sql);

        public abstract int delete(Connection con, String sql);

        public abstract int update(Connection con, String sql);

        public abstract ArrayList<HashMap<String, Object>> select(Connection con, String sql);

        public abstract void release();
}