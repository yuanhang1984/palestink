package module.file_storage.necessary;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.ServletContext;
import framework.ext.factory.DbFactory;
import framework.sdk.Framework;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.DaemonAction;
import framework.sdbo.object.SqlRepository;
import library.thread.Block;
import library.database.DatabaseKit;
import org.dom4j.Element;

class ExecuteThread extends Thread {
        private Connection connection;

        public ExecuteThread() {
                this.connection = DbFactory.getConnection();
        }

        private void deleteExpireFile(ArrayList<String> list) {
                HashMap<String, Object> p = null;
                String sql = null;
                int res = 0;
                Element sqlRoot = SqlRepository.get("file_storage");
                if (null == sqlRoot) {
                        Framework.LOG.error(Daemon.MODULE_NAME, Message.transformStatus(Message.RESULT.NO_MODULE_SQL));
                        return;
                }
                try {
                        this.connection.setAutoCommit(false);
                        Iterator<String> iter = list.iterator();
                        while (iter.hasNext()) {
                                String uuid = iter.next();
                                p = new HashMap<String, Object>();
                                p.put("uuid", uuid);
                                sql = DatabaseKit.composeSql(sqlRoot, "deleteStorageFile", p);
                                if (0 >= sql.trim().length()) {
                                        Framework.LOG.error(Daemon.MODULE_NAME, Message.transformStatus(Message.RESULT.COMPOSE_SQL_ERROR) + "[deleteStorageFile]");
                                        this.connection.rollback();
                                        return;
                                }
                                res = DbFactory.iduExecute(this.connection, sql);
                                if (0 >= res) {
                                        Framework.LOG.error(Daemon.MODULE_NAME, "Delete Storage File Error");
                                        this.connection.rollback();
                                        return;
                                }
                                p = new HashMap<String, Object>();
                                p.put("file_uuid", uuid);
                                sql = DatabaseKit.composeSql(sqlRoot, "deleteStorageRepository", p);
                                if (0 >= sql.trim().length()) {
                                        Framework.LOG.error(Daemon.MODULE_NAME, Message.transformStatus(Message.RESULT.COMPOSE_SQL_ERROR) + "[deleteStorageRepository]");
                                        this.connection.rollback();
                                        return;
                                }
                                res = DbFactory.iduExecute(this.connection, sql);
                                if (0 >= res) {
                                        Framework.LOG.error(Daemon.MODULE_NAME, "Delete Storage Repository Error");
                                        this.connection.rollback();
                                        return;
                                }
                                this.connection.commit();
                        }
                } catch (Exception e) {
                        Framework.LOG.warn(Daemon.MODULE_NAME, e.toString());
                        try {
                                this.connection.rollback();
                        } catch (Exception e2) {
                                Framework.LOG.warn(Daemon.MODULE_NAME, e2.toString());
                        }
                }
        }

        @Override
        public void run() {
                try {
                        Block.lock(Daemon.LOCK, Daemon.CONDITION);
                } catch (Exception e) {
                        Framework.LOG.error(Daemon.MODULE_NAME, e.toString());
                        return;
                }
                Framework.LOG.info(Daemon.MODULE_NAME, "File Storage Daemon Start");
                Element sqlRoot = SqlRepository.get("file_storage");
                if (null == sqlRoot) {
                        Framework.LOG.error(Daemon.MODULE_NAME, Message.transformStatus(Message.RESULT.NO_MODULE_SQL));
                        return;
                }
                HashMap<String, Object> p = new HashMap<String, Object>();
                p.put("under_expire_datetime", new java.sql.Timestamp(System.currentTimeMillis()));
                String sql = DatabaseKit.composeSql(sqlRoot, "selectStorageFile", p);
                if (0 >= sql.trim().length()) {
                        Framework.LOG.error(Daemon.MODULE_NAME, Message.transformStatus(Message.RESULT.COMPOSE_SQL_ERROR));
                        return;
                }
                try {
                        for (;;) {
                                ArrayList<String> expireFileUuidList = new ArrayList<String>();
                                try {
                                        // 如果休眠设置在最后，出现异常时会频繁的执行操作。
                                        // Thread.sleep(Config.FILE_CHECK_TIME_CYCLE * 1000 * 60);
                                        Thread.sleep(1000 * 10);
                                        ArrayList<HashMap<String, Object>> list = DbFactory.select(this.connection, sql);
                                        if (1 <= list.size()) {
                                                Iterator<HashMap<String, Object>> iter = list.iterator();
                                                while (iter.hasNext()) {
                                                        HashMap<String, Object> m = iter.next();
                                                        String uuid = (String) m.get("uuid");
                                                        expireFileUuidList.add(uuid);
                                                }
                                        }
                                } catch (InterruptedException e) {
                                        Framework.LOG.info(Daemon.MODULE_NAME, "File Storage Daemon Stop");
                                        return;
                                } catch (Exception e) {
                                        Framework.LOG.warn(Daemon.MODULE_NAME, e.toString());
                                }
                                this.deleteExpireFile(expireFileUuidList);
                        }
                } finally {
                        try {
                                this.connection.close();
                        } catch (Exception e) {
                                Framework.LOG.warn(Daemon.MODULE_NAME, e.toString());
                        }
                }
        }
}

public class Daemon extends DaemonAction {
        public static final String MODULE_NAME = "file_storage.Daemon";
        private ExecuteThread thread;
        public static Lock LOCK = new ReentrantLock();
        public static Condition CONDITION = LOCK.newCondition();

        public Daemon(ServletContext servletContext) {
                super(servletContext);
                this.thread = new ExecuteThread();
                // 添加守护进程对象至守护进程列表，以便于释放资源。
                DaemonAction.DAEMON_THREAD_LIST.add(this.thread);
        }

        @Override
        public void run() {
                // run方法由Listener调用，所以这里不能加线程锁，否则整个应用会堵塞在Listener调用run方法的地方。
                this.thread.start();
        }
}