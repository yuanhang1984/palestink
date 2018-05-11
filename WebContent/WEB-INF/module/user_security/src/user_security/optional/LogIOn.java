package module.user_security.optional;

import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dom4j.Element;
import framework.sdk.Framework;
import framework.sdk.msg.Message;
import framework.ext.factory.DbFactory;
import framework.sdbo.object.SqlRepository;
import framework.sdk.spec.module.necessary.CustomAction;
import library.database.DatabaseKit;
import library.string.CharacterString;
import module.user_security.necessary.Config;

public class LogIOn extends CustomAction {
        private HttpServletRequest httpServletRequest;
        // private HttpServletResponse httpServletResponse;
        private HashMap<String, Object> parameter;
        private Connection connection;

        public LogIOn(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                this.httpServletRequest = httpServletRequest;
                // this.httpServletResponse = httpServletResponse;
                this.parameter = parameter;
                this.connection = connection;
        }

        /**
         * 登录后，保存用户信息
         * 
         * @param hm 包含用户数据的HashMap
         */
        private void saveUserSession(HashMap<String, Object> hm) {
                // 首先清除原有登录的信息
                this.cleanUserSession();
                this.httpServletRequest.getSession().setAttribute(Framework.USER_UUID, (String) hm.get("uuid"));
                this.httpServletRequest.getSession().setAttribute(Framework.USER_ROLE, (String) hm.get("role"));
        }

        /**
         * 退出前，清除用户信息
         */
        private void cleanUserSession() {
                this.httpServletRequest.getSession().setAttribute(Framework.USER_UUID, null);
                this.httpServletRequest.getSession().setAttribute(Framework.USER_ROLE, null);
                this.httpServletRequest.getSession().removeAttribute(Framework.USER_UUID);
                this.httpServletRequest.getSession().removeAttribute(Framework.USER_ROLE);
        }

        /**
         * 登录
         * 
         * [参数列表所需参数]
         * name: 用户名
         * password: 密码
         */
        public Message login() {
                Message msg = new Message();
                HashMap<String, Object> p = null;
                String sql = null;
                ArrayList<HashMap<String, Object>> list = null;
                int res = 0;
                Element sqlRoot = SqlRepository.get("user_security");
                if (null == sqlRoot) {
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail("user_security");
                        return msg;
                }
                try {
                        p = new HashMap<String, Object>();
                        p.put("name", parameter.get("name"));
                        p.put("password", parameter.get("password"));
                        sql = DatabaseKit.composeSql(sqlRoot, "selectUser", p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail("selectUser");
                                return msg;
                        }
                        list = DbFactory.select(this.connection, sql);
                        if (0 < list.size()) {
                                // 登录“检索”成功
                                // 判断用户状态
                                HashMap<String, Object> hm = list.iterator().next();
                                if (1 == (Integer) hm.get("status")) {
                                        // 冻结时间已过，解冻账号。
                                        p = new HashMap<String, Object>();
                                        p.put("uuid", hm.get("uuid"));
                                        p.put("failed_retry_count", 0);
                                        p.put("sn_frozen_datetime", "set_null");
                                        sql = DatabaseKit.composeSql(sqlRoot, "updateUser", p);
                                        if (0 >= sql.trim().length()) {
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                                msg.setDetail("updateUser");
                                                return msg;
                                        }
                                        res = DbFactory.iduExecute(this.connection, sql);
                                        if (0 >= res) {
                                                // 修改账户状态失败。
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.OTHER);
                                                msg.setDetail("Modify Account Status Error");
                                                return msg;
                                        }
                                        // 用户正常，符合登录要求。
                                        this.saveUserSession(hm);
                                        msg.setStatus(Message.STATUS.SUCCESS);
                                        msg.setError(Message.ERROR.NONE);
                                        return msg;
                                } else if (2 == (Integer) hm.get("status")) {
                                        // 用户冻结
                                        // 判断冻结时间是否已过
                                        Timestamp frozenDateTime = ((Timestamp) hm.get("frozen_datetime"));
                                        if (frozenDateTime.getTime() + (Config.ACCOUNT_FROZEN_TIME * 1000 * 60) < new Date().getTime()) {
                                                // 冻结时间已过，解冻账号。
                                                p = new HashMap<String, Object>();
                                                p.put("uuid", hm.get("uuid"));
                                                p.put("failed_retry_count", 0);
                                                p.put("sn_frozen_datetime", "set_null");
                                                p.put("status", 1);
                                                sql = DatabaseKit.composeSql(sqlRoot, "updateUser", p);
                                                if (0 >= sql.trim().length()) {
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                                        msg.setDetail("updateUser");
                                                        return msg;
                                                }
                                                res = DbFactory.iduExecute(this.connection, sql);
                                                if (0 >= res) {
                                                        // 解冻账户失败。
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.OTHER);
                                                        msg.setDetail("Unfrozen Account Error");
                                                        return msg;
                                                }
                                                // 解冻账户成功，符合登录要求。
                                                this.saveUserSession(hm);
                                                msg.setStatus(Message.STATUS.SUCCESS);
                                                msg.setError(Message.ERROR.NONE);
                                                return msg;
                                        }
                                        // 冻结时间未过，不符合登录要求。
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("Account Has Been Frozen");
                                        return msg;
                                } else if (3 == (Integer) hm.get("status")) {
                                        // 账户被锁定
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("Account Has Been Locked");
                                        return msg;
                                } else {
                                        // 账户状态异常
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("Account Status Exception");
                                        return msg;
                                }
                        } else {
                                // 用户名和密码没有匹配到数据有两种可能：
                                // [1]账户不存在
                                // [2]密码错误
                                // 这里需要对第2中情况增加登录失败重试次数。
                                p = new HashMap<String, Object>();
                                p.put("name", parameter.get("name"));
                                p.put("status", 1); // 查询状态为“正常”的用户
                                sql = DatabaseKit.composeSql(sqlRoot, "selectUser", p);
                                if (0 >= sql.trim().length()) {
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                        msg.setDetail("selectUser");
                                        return msg;
                                }
                                list = DbFactory.select(this.connection, sql);
                                if (0 >= list.size()) {
                                        // 这里应该返回没有账户才对，但考虑到安全因素，所以返回密码错误。
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("Password Error");
                                        return msg;
                                }
                                // 账户存在。
                                HashMap<String, Object> hm = list.iterator().next();
                                // 获取当前的“失败重试计数”，并且增加1次失败记录。
                                Integer count = ((Integer) hm.get("failed_retry_count")) + 1;
                                // 判断是否超过最大重试计数限制
                                res = Config.LOGIN_FAILED_RETRY_COUNT - count;
                                if (0 < res) {
                                        // 如果没有超过限制技术，那么增加失败重试计数。并且给予密码错误的提示信息。
                                        p = new HashMap<String, Object>();
                                        p.put("uuid", hm.get("uuid"));
                                        p.put("failed_retry_count", count);
                                        sql = DatabaseKit.composeSql(sqlRoot, "updateUser", p);
                                        if (0 >= sql.trim().length()) {
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                                msg.setDetail("updateUser");
                                                return msg;
                                        }
                                        res = DbFactory.iduExecute(this.connection, sql);
                                        if (0 >= res) {
                                                // 增加“失败重试计数”失败。
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.OTHER);
                                                msg.setDetail("Increase Failed Retry Count Error");
                                                return msg;
                                        }
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("Password Error");
                                        return msg;
                                } else {
                                        // 如果已经超过规定值，执行以下操作：冻结账号；失败重试计数归零；返回账户冻结信息。
                                        p = new HashMap<String, Object>();
                                        p.put("uuid", hm.get("uuid"));
                                        p.put("failed_retry_count", 0);
                                        p.put("frozen_datetime", new Timestamp(System.currentTimeMillis()));
                                        p.put("status", 2);
                                        sql = DatabaseKit.composeSql(sqlRoot, "updateUser", p);
                                        if (0 >= sql.trim().length()) {
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                                msg.setDetail("updateUser");
                                                return msg;
                                        }
                                        res = DbFactory.iduExecute(this.connection, sql);
                                        if (0 >= res) {
                                                // 冻结账户失败。
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.OTHER);
                                                msg.setDetail("Frozen Account Error");
                                                return msg;
                                        }
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("Account Frozen");
                                        return msg;
                                }
                        }
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         * 退出
         * 
         * [参数列表所需参数]
         */
        public Message logout() {
                Message msg = new Message();
                this.cleanUserSession();
                msg.setStatus(Message.STATUS.SUCCESS);
                msg.setError(Message.ERROR.NONE);
                return msg;
        }
}