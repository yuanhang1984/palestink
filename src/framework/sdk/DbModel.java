package framework.sdk;

import java.sql.Connection;

public abstract class DbModel {
        public abstract boolean init(String driver, String url, String name, String password, int maxActiveConnection);

        public abstract Connection getConnection();

        public abstract void release();
}