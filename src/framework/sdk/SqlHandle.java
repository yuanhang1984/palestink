package framework.sdk;

public abstract class SqlHandle {
        /**
         * 获取SqlModel
         * 
         * @param autoCommit 如果为true，直接提交不开启事务；如果为false则开启事务，需要执行commit操作才能提交数据。
         * @return 数据库工厂对象
         */
        public abstract DbModel getSqlModel(boolean autoCommit);
}
