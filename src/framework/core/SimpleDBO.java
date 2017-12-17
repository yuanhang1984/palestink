package framework.core;

import java.sql.Connection;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map.Entry;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import library.database.DatabaseKit;
import framework.sdk.DbInstanceModel;
import framework.sdk.Message;
import framework.sdk.Framework;
import framework.sdk.SqlHandle;
import framework.db.sdbo.DbFactory;
import framework.sdbo.object.Namespace;
import framework.sdbo.object.SqlRepository;
import org.dom4j.Element;
import org.json.JSONArray;
import org.json.JSONObject;

public class SimpleDBO extends SqlHandle {
        private static final String MODULE_NAME = "SimpleDBO";

        /*
         * 服务端的HttpServlet
         */
        private HttpServlet httpServlet;

        /*
         * dispatch中的namespace
         */
        private String namespace;

        /*
         * 传入的参数
         */
        private HashMap<String, Object> parameter;

        /*
         * 向客户端发送数据的HttpServletResponse
         */
        private HttpServletRequest request;

        /*
         * 向客户端发送数据的HttpServletResponse
         */
        private HttpServletResponse response;

        /**
         * 生成对象的构造函数
         */
        public SimpleDBO() {
                super();
        }

        /**
         * 生成对象的构造函数
         */
        public SimpleDBO(HttpServlet httpServlet, String namespace, HashMap<String, Object> parameter, HttpServletRequest request, HttpServletResponse response) {
                this.httpServlet = httpServlet;
                this.namespace = namespace;
                this.parameter = parameter;
                this.request = request;
                this.response = response;
        }

        /**
         * idu执行（用于insert、delete、update三种操作）
         * @param moduleName 模块的名称
         * @param id sql语句的id
         */
        private Message iduExecute(Connection c, String moduleName, String id, HashMap<String, Object> parameter) {
                Message msg = new Message();
                try {
                        Element sqlRoot = SqlRepository.get(moduleName);
                        if (null == sqlRoot) {
                                msg.setResult(Message.RESULT.NO_MODULE_SQL);
                                return msg;
                        }
                        String sql = DatabaseKit.composeSql(sqlRoot, id, parameter);
                        if (0 >= sql.trim().length()) {
                                msg.setResult(Message.RESULT.COMPOSE_SQL_ERROR);
                                return msg;
                        }
                        int res = DbFactory.iduExecute(c, sql);
                        msg.setCount(res);
                        msg.setResult(Message.RESULT.SUCCESS);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                        msg.setResult(Message.RESULT.EXCEPTION);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * select操作
         * @param moduleName 模块的名称
         * @param id sql语句的id
         */
        private Message select(Connection c, String moduleName, String id, HashMap<String, Object> parameter) {
                Message msg = new Message();
                try {
                        Element sqlRoot = SqlRepository.get(moduleName);
                        if (null == sqlRoot) {
                                msg.setResult(Message.RESULT.NO_MODULE_SQL);
                                return msg;
                        }
                        String sql = DatabaseKit.composeSql(sqlRoot, id, parameter);
                        if (0 >= sql.trim().length()) {
                                msg.setResult(Message.RESULT.COMPOSE_SQL_ERROR);
                                return msg;
                        }
                        JSONArray a = new JSONArray();
                        ArrayList<HashMap<String, Object>> list = DbFactory.select(c, sql);
                        msg.setDataList(list);
                        Iterator<HashMap<String, Object>> iter = list.iterator();
                        while (iter.hasNext()) {
                                HashMap<String, Object> hm = iter.next();
                                Iterator<Entry<String, Object>> iter2 = hm.entrySet().iterator();
                                JSONObject row = new JSONObject();
                                while (iter2.hasNext()) {
                                        Entry<String, Object> e = iter2.next();
                                        // 当所在列没有数据的时候，返回列名和null（弥补mybatis检索无数据没有返回key的缺陷）。
                                        if (null != e.getValue()) {
                                                row.put(e.getKey(), e.getValue());
                                        } else {
                                                row.put(e.getKey(), JSONObject.NULL);
                                        }
                                }
                                a.put(row);
                        }
                        msg.setResult(Message.RESULT.SUCCESS);
                        msg.setCount(a.length());
                        msg.setDetail(a.toString());
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                        msg.setResult(Message.RESULT.EXCEPTION);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
        * 获取sql中所需的参数
        * @param param 配置中要求的参数数组
        * @return 返回当前事务操作分组的参数
        */
        private HashMap<String, Object> getSqlParameter(String param[]) {
                HashMap<String, Object> hm = new HashMap<String, Object>();
                Iterator<Entry<String, Object>> iter = this.parameter.entrySet().iterator();
                while (iter.hasNext()) {
                        Entry<String, Object> e = iter.next();
                        String name = e.getKey();
                        for (int i = 0; i < param.length; i++) {
                                // 拆分参数别名
                                if (-1 == param[i].indexOf("->")) {
                                        // 没有别名
                                        if (param[i].equalsIgnoreCase(name)) {
                                                hm.put(e.getKey(), e.getValue());
                                                break;
                                        }
                                } else {
                                        // 拥有别名
                                        String arr[] = param[i].split("->");
                                        if (arr[0].equalsIgnoreCase(name)) {
                                                hm.put(arr[1], e.getValue());
                                                break;
                                        }
                                }
                        }
                }
                return hm;
        }

        /**
         * 事务的idu操作
         * @param c 数据库的Connection
         * @param type 类型
         * @param moduleName 模块的名称
         * @param id sql文件中的id
         * @param sqlParameter 组合sql所需的参数
         * @param flip 数据库操作结果是否取反 true:是 false:否
         * @param insist 是否不关心结果继续执行 true:是 false:否
         * @return 操作正确返回true，失败返回false。
         */
        private boolean transactionIdu(Connection c, String type, String moduleName, String id, HashMap<String, Object> sqlParameter, boolean flip, boolean insist) {
                try {
                        Message m = this.iduExecute(c, moduleName, id, sqlParameter);
                        if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                c.rollback();
                                Message.send(request, response, m.getResult(), null, "[" + type + "][" + id + "]");
                                return false;
                        }
                        if (insist) {
                                // 不对结果进行判断，直接返回true。
                                return true;
                        }
                        if (!flip) {
                                if (0 >= m.getCount()) {// idu的执行结果未必大于0才是正确，但这里是以事务的方式来处理，所以要以是否大于0作为判断依据。
                                        c.rollback();
                                        Message.send(request, response, Message.RESULT.IDU_NO_DATA, null, "[" + type + "][" + id + "]");
                                        return false;
                                }
                        } else {
                                if (0 < m.getCount()) {// 取反
                                        c.rollback();
                                        Message.send(request, response, Message.RESULT.IDU_NO_DATA, null, "[" + type + "][" + id + "]");
                                        return false;
                                }
                        }
                        return true;
                } catch (Exception e) {
                        try {
                                c.rollback();
                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                                Message.send(request, response, Message.RESULT.EXCEPTION, null, e.toString());
                                return false;
                        } catch (Exception e2) {
                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e2.toString());
                                Message.send(request, response, Message.RESULT.EXCEPTION, null, e2.toString());
                                return false;
                        }
                }
        }

        /**
         * 事务中foreach的idu操作
         * @param aliasNameMap 存放别名的HashMap
         * @param c 数据库的Connection
         * @param type 类型
         * @param moduleName 模块的名称
         * @param id sql文件中的id
         * @param sqlParameter 组合sql所需的参数
         * @param flip 数据库操作结果是否取反 true:是 false:否
         * @param insist 是否不关心结果继续执行 true:是 false:否
         * @return 操作正确返回true，失败返回false。
         */
        private boolean foreachIdu(HashMap<String, ArrayList<HashMap<String, Object>>> aliasNameMap, Connection c, String type, String moduleName, String id, HashMap<String, Object> sqlParameter, boolean flip, boolean insist) {
                try {
                        if (2 != type.split("<<").length) {
                                c.rollback();
                                Message.send(request, response, Message.RESULT.RESULT_ALIAS_ERROR, null, "[" + type + "][" + id + "]");
                                return false;
                        }
                        if (2 != type.split("<<")[1].split(":").length) {
                                c.rollback();
                                Message.send(request, response, Message.RESULT.RESULT_PARAMETER_ERROR, null, "[" + type + "][" + id + "]");
                                return false;
                        }
                        // 结果集别名
                        String aliasName = type.split("<<")[1].split(":")[0];
                        ArrayList<HashMap<String, Object>> list = aliasNameMap.get(aliasName);
                        if (null == list) {
                                c.rollback();
                                Message.send(request, response, Message.RESULT.RESULT_NOT_EXIST, null, "[" + type + "][" + id + "]");
                                return false;
                        }
                        Iterator<HashMap<String, Object>> iter = list.iterator();
                        while (iter.hasNext()) {
                                HashMap<String, Object> hm = iter.next();
                                // 结果集引用数据的参数名
                                String[] aliasParameterList = type.split("<<")[1].split(":")[1].split(",");
                                for (int i = 0; i < aliasParameterList.length; i++) {
                                        if (-1 == aliasParameterList[i].indexOf("->")) {
                                                sqlParameter.put(aliasParameterList[i], hm.get(aliasParameterList[i]));
                                        } else {
                                                // 替换别名
                                                String s = aliasParameterList[i].split("->")[1];
                                                sqlParameter.put(s, hm.get(aliasParameterList[i]));
                                        }
                                }
                                Message m = this.iduExecute(c, moduleName, id, sqlParameter);
                                if (!insist) {
                                        if (!flip) {
                                                if (0 >= m.getCount()) {// idu的执行结果未必大于0才是正确，但这里是以事务的方式来处理，所以要以是否大于0作为判断依据。
                                                        c.rollback();
                                                        Message.send(request, response, Message.RESULT.IDU_NO_DATA, null, "[" + type + "][" + id + "]");
                                                        return false;
                                                }
                                        } else {
                                                if (0 < m.getCount()) {// 取反
                                                        c.rollback();
                                                        Message.send(request, response, Message.RESULT.IDU_NO_DATA, null, "[" + type + "][" + id + "]");
                                                        return false;
                                                }
                                        }
                                }
                        }
                        return true;
                } catch (Exception e) {
                        try {
                                c.rollback();
                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                                Message.send(request, response, Message.RESULT.EXCEPTION, null, e.toString());
                                return false;
                        } catch (Exception e2) {
                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e2.toString());
                                Message.send(request, response, Message.RESULT.EXCEPTION, null, e2.toString());
                                return false;
                        }
                }
        }

        /**
         * 递归select
         * @param c 数据库的Connection
         * @param moduleName 模块的名称
         * @param id sql文件中的id
         * @param sqlParameter 组合sql所需的参数
         * @param idColumnName 标记编号名称（如：id）
         * @param pidColumnName 标记父级编号名称（如：parent_id）
         * @param list 最终返回的数据对象
         * @return 操作正确返回true，失败返回false。
         */
        private boolean recursiveSelect(Connection c, String moduleName, String id, HashMap<String, Object> sqlParameter, String idColumnName, String pidColumnName, ArrayList<HashMap<String, Object>> list) {
                Message m = this.select(c, moduleName, id, sqlParameter);
                if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                        return false;
                }
                Iterator<HashMap<String, Object>> iter = m.getDataList().iterator();
                while (iter.hasNext()) {
                        HashMap<String, Object> hm = iter.next();
                        Object idObj = hm.get(idColumnName);
                        HashMap<String, Object> p = new HashMap<String, Object>();
                        p.put(pidColumnName, idObj);
                        if (!this.recursiveSelect(c, moduleName, id, p, idColumnName, pidColumnName, list)) {
                                return false;
                        }
                        list.add(hm);
                }
                return true;
        }

        /**
         * （配置）transaction操作
         * 
         * @return 操作成功，向客户端输出字符串1<br />
         *         操作失败，向客户端输出字符串0<br />
         *         发生异常，向客户端输出字符串-1
         */
        public void transaction() {
                Connection c = null;
                try {
                        ArrayList<Namespace> namespaceList = Namespace.analyseNamespace(this.namespace);
                        if (0 >= namespaceList.size()) {
                                Message.send(request, response, Message.RESULT.ANALYSE_NAMESPACE_ERROR, null, null);
                                return;
                        }
                        c = DbFactory.getConnection();
                        if (null == c) {
                                Message.send(request, response, Message.RESULT.DATABASE_CONNECTION_ERROR, null, null);
                                return;
                        }
                        c.setAutoCommit(false);
                        HashMap<String, ArrayList<HashMap<String, Object>>> aliasNameMap = new HashMap<String, ArrayList<HashMap<String, Object>>>();
                        Iterator<Namespace> namespaceIter = namespaceList.iterator();
                        int namespaceIndex = 0;
                        while (namespaceIter.hasNext()) {
                                Namespace ns = namespaceIter.next();
                                String type = ns.getType();
                                String moduleName = ns.getModuleName();
                                String id = ns.getId();
                                String[] parameterList = ns.getParameterList();
                                HashMap<String, Object> sqlParameter = null;
                                if (parameterList[0].equalsIgnoreCase(Namespace.NO_PARAMETER)) {
                                        // 如果没有参数，那么把所有的变量都给sqlParameter，尽管它也用不到，但在这里是一种规范。
                                        sqlParameter = this.parameter;
                                } else {
                                        // 如果有参数，那么需要转换参数中的别名
                                        sqlParameter = this.getSqlParameter(parameterList);
                                }
                                if ((type.equalsIgnoreCase("insert")) || (type.equalsIgnoreCase("delete")) || (type.equalsIgnoreCase("update"))) {// idu操作
                                        if (!this.transactionIdu(c, type, moduleName, id, sqlParameter, false, false)) {
                                                return;
                                        }
                                } else if ((type.equalsIgnoreCase("!insert")) || (type.equalsIgnoreCase("!delete")) || (type.equalsIgnoreCase("!update"))) {// idu操作结果取反
                                        if (!this.transactionIdu(c, type, moduleName, id, sqlParameter, true, false)) {
                                                return;
                                        }
                                } else if ((type.equalsIgnoreCase("insist-insert")) || (type.equalsIgnoreCase("insist-delete")) || (type.equalsIgnoreCase("insist-update"))) {// idu不计操作结果
                                        if (!this.transactionIdu(c, type, moduleName, id, sqlParameter, false, true)) {
                                                return;
                                        }
                                } else if ((type.toLowerCase().startsWith("foreach-insert<<")) || (type.toLowerCase().startsWith("foreach-delete<<")) || (type.toLowerCase().startsWith("foreach-update<<"))) {
                                        if (!this.foreachIdu(aliasNameMap, c, type, moduleName, id, sqlParameter, false, false)) {
                                                return;
                                        }
                                } else if ((type.toLowerCase().startsWith("foreach-!insert<<")) || (type.toLowerCase().startsWith("foreach-!delete<<")) || (type.toLowerCase().startsWith("foreach-!update<<"))) {
                                        if (!this.foreachIdu(aliasNameMap, c, type, moduleName, id, sqlParameter, true, false)) {
                                                return;
                                        }
                                } else if ((type.toLowerCase().startsWith("foreach-insist-insert<<")) || (type.toLowerCase().startsWith("foreach-insist-delete<<")) || (type.toLowerCase().startsWith("foreach-insist-update<<"))) {
                                        if (!this.foreachIdu(aliasNameMap, c, type, moduleName, id, sqlParameter, false, true)) {
                                                return;
                                        }
                                } else if (type.equalsIgnoreCase("select")) {// select操作
                                        Message m = this.select(c, moduleName, id, sqlParameter);
                                        if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                c.rollback();
                                                Message.send(request, response, m.getResult(), null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (0 >= m.getCount()) {// select的执行结果未必大于0才是正确，但这里是以事务的方式来处理，所以要以是否大于0作为判断依据。
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.QUERY_NO_DATA, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if ((namespaceIndex + 1) >= namespaceList.size()) {
                                                // 最后一个namespace，可以直接返回select的结果集。
                                                c.commit();
                                                Message.send(request, response, Message.RESULT.SUCCESS, m.getCount(), m.getDetail());
                                                return;
                                        }
                                } else if (type.equalsIgnoreCase("!select")) {// !select操作
                                        Message m = this.select(c, moduleName, id, sqlParameter);
                                        if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                c.rollback();
                                                Message.send(request, response, m.getResult(), null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (0 < m.getCount()) {// 取反
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.QUERY_NO_DATA, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                } else if (type.toLowerCase().startsWith("select>>")) {
                                        if (2 != type.split(">>").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_ALIAS_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        String aliasName = type.split(">>")[1];
                                        Message m = this.select(c, moduleName, id, sqlParameter);
                                        if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                c.rollback();
                                                Message.send(request, response, m.getResult(), null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (0 >= m.getCount()) {// select的执行结果未必大于0才是正确，但这里是以事务的方式来处理，所以要以是否大于0作为判断依据。
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.QUERY_NO_DATA, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        ArrayList<HashMap<String, Object>> list = m.getDataList();
                                        aliasNameMap.put(aliasName, list);
                                } else if (type.toLowerCase().startsWith("recursive-select>>")) {
                                        if (2 != type.split(">>").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_ALIAS_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (2 != type.split(">>")[1].split(":").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_PARAMETER_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (2 != type.split(">>")[1].split(":")[1].split(",").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_PARAMETER_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        String aliasName = type.split(">>")[1].split(":")[0];
                                        String idColumnName = type.split(">>")[1].split(":")[1].split(",")[0];
                                        String pidColumnName = type.split(">>")[1].split(":")[1].split(",")[0];
                                        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                                        // ArrayList<Object> list = new ArrayList<Object>();
                                        if (!this.recursiveSelect(c, moduleName, id, sqlParameter, idColumnName, pidColumnName, list)) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RECURSIVE_SELECT_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (0 >= list.size()) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.QUERY_NO_DATA, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        aliasNameMap.put(aliasName, list);
                                } else if (type.toLowerCase().startsWith("foreach-select<<")) {// 读取结果集后执行select操作
                                        if (2 != type.split("<<").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_ALIAS_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (2 != type.split("<<")[1].split(":").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_PARAMETER_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        // 结果集别名
                                        String aliasName = type.split("<<")[1].split(":")[0];
                                        ArrayList<HashMap<String, Object>> list = aliasNameMap.get(aliasName);
                                        if (null == list) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_NOT_EXIST, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        Iterator<HashMap<String, Object>> iter = list.iterator();
                                        ArrayList<Message> retList = new ArrayList<Message>();
                                        while (iter.hasNext()) {
                                                HashMap<String, Object> hm = iter.next();
                                                // 结果集引用数据的参数名
                                                String[] aliasParameterList = type.split(">>")[1].split(":")[1].split(",");
                                                for (int i = 0; i < aliasParameterList.length; i++) {
                                                        if (-1 == aliasParameterList[i].indexOf("->")) {
                                                                sqlParameter.put(aliasParameterList[i], hm.get(aliasParameterList[i]));
                                                        } else {
                                                                // 替换别名
                                                                String s = aliasParameterList[i].split("->")[1];
                                                                sqlParameter.put(s, hm.get(aliasParameterList[i]));
                                                        }
                                                }
                                                Message m = this.select(c, moduleName, id, sqlParameter);
                                                if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                        c.rollback();
                                                        Message.send(request, response, m.getResult(), null, "[" + type + "][" + id + "]");
                                                        return;
                                                }
                                                if (0 >= m.getCount()) {// select的执行结果未必大于0才是正确，但这里是以事务的方式来处理，所以要以是否大于0作为判断依据。
                                                        c.rollback();
                                                        Message.send(request, response, Message.RESULT.QUERY_NO_DATA, null, "[" + type + "][" + id + "]");
                                                        return;
                                                }
                                                // 如果是最后一个namespace，那么就添加这个数据。
                                                if ((namespaceIndex + 1) >= namespaceList.size()) {
                                                        retList.add(m);
                                                }
                                        }
                                        if ((namespaceIndex + 1) >= namespaceList.size()) {
                                                // 最后一个namespace，可以直接返回select的结果集。
                                                c.commit();
                                                JSONArray a = new JSONArray();
                                                Iterator<Message> retIter = retList.iterator();
                                                while (retIter.hasNext()) {
                                                        Message m = retIter.next();
                                                        ArrayList<HashMap<String, Object>> dataList = m.getDataList();
                                                        Iterator<HashMap<String, Object>> dataIter = dataList.iterator();
                                                        while (dataIter.hasNext()) {
                                                                HashMap<String, Object> hm = dataIter.next();
                                                                Iterator<Entry<String, Object>> rowIter = hm.entrySet().iterator();
                                                                JSONObject row = new JSONObject();
                                                                while (rowIter.hasNext()) {
                                                                        Entry<String, Object> e = rowIter.next();
                                                                        // 当所在列没有数据的时候，返回列名和null（弥补mybatis检索无数据没有返回key的缺陷）。
                                                                        if (null != e.getValue()) {
                                                                                row.put(e.getKey(), e.getValue());
                                                                        } else {
                                                                                row.put(e.getKey(), JSONObject.NULL);
                                                                        }
                                                                }
                                                                a.put(row);
                                                        }
                                                }
                                                Message.send(request, response, Message.RESULT.SUCCESS, a.length(), a.toString());
                                                return;
                                        }
                                } else if (type.toLowerCase().startsWith("foreach-!select<<")) {// 读取结果集后执行!select操作
                                        if (2 != type.split("<<").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_ALIAS_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (2 != type.split("<<")[1].split(":").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_PARAMETER_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        // 结果集别名
                                        String aliasName = type.split("<<")[1].split(":")[0];
                                        ArrayList<HashMap<String, Object>> list = aliasNameMap.get(aliasName);
                                        if (null == list) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_NOT_EXIST, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        Iterator<HashMap<String, Object>> iter = list.iterator();
                                        while (iter.hasNext()) {
                                                HashMap<String, Object> hm = iter.next();
                                                // 结果集引用数据的参数名
                                                String[] aliasParameterList = type.split(">>")[1].split(":")[1].split(",");
                                                for (int i = 0; i < aliasParameterList.length; i++) {
                                                        if (-1 == aliasParameterList[i].indexOf("->")) {
                                                                sqlParameter.put(aliasParameterList[i], hm.get(aliasParameterList[i]));
                                                        } else {
                                                                // 替换别名
                                                                String s = aliasParameterList[i].split("->")[1];
                                                                sqlParameter.put(s, hm.get(aliasParameterList[i]));
                                                        }
                                                }
                                                Message m = this.select(c, moduleName, id, sqlParameter);
                                                if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                        c.rollback();
                                                        Message.send(request, response, m.getResult(), null, "[" + type + "][" + id + "]");
                                                        return;
                                                }
                                                if (0 < m.getCount()) {// 取反
                                                        c.rollback();
                                                        Message.send(request, response, Message.RESULT.QUERY_NO_DATA, null, "[" + type + "][" + id + "]");
                                                        return;
                                                }
                                        }
                                } else if (type.toLowerCase().startsWith("foreach-select>>")) {
                                        if (2 != type.split(">>").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_ALIAS_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (2 != type.split(">>")[1].split(":").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_PARAMETER_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        // 结果集别名
                                        String aliasName = type.split(">>")[1].split(":")[0];
                                        ArrayList<HashMap<String, Object>> list = aliasNameMap.get(aliasName);
                                        if (null == list) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_NOT_EXIST, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        Iterator<HashMap<String, Object>> iter = list.iterator();
                                        while (iter.hasNext()) {
                                                HashMap<String, Object> hm = iter.next();
                                                // 结果集引用数据的参数名
                                                String[] aliasParameterList = type.split(">>")[1].split(":")[1].split(",");
                                                for (int i = 0; i < aliasParameterList.length; i++) {
                                                        if (-1 == aliasParameterList[i].indexOf("->")) {
                                                                sqlParameter.put(aliasParameterList[i], hm.get(aliasParameterList[i]));
                                                        } else {
                                                                // 替换别名
                                                                String s = aliasParameterList[i].split("->")[1];
                                                                sqlParameter.put(s, hm.get(aliasParameterList[i]));
                                                        }
                                                }
                                                Message m = this.select(c, moduleName, id, sqlParameter);
                                                if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                        c.rollback();
                                                        Message.send(request, response, m.getResult(), null, "[" + type + "][" + id + "]");
                                                        return;
                                                }
                                                if (0 >= m.getCount()) {// select的执行结果未必大于0才是正确，但这里是以事务的方式来处理，所以要以是否大于0作为判断依据。
                                                        c.rollback();
                                                        Message.send(request, response, Message.RESULT.QUERY_NO_DATA, null, "[" + type + "][" + id + "]");
                                                        return;
                                                }
                                                aliasNameMap.put(aliasName, m.getDataList());
                                        }
                                } else if (type.equalsIgnoreCase("custom")) {
                                        Object[] params = { this.httpServlet, this.request, this.response, c, sqlParameter };
                                        Class<?>[] paramsType = { HttpServlet.class, HttpServletRequest.class, HttpServletResponse.class, Connection.class, HashMap.class };
                                        try {
                                                Class<?> businessClass = null;
                                                Method classMethod = null;
                                                if (3 != id.split("\\.").length) {
                                                        c.rollback();
                                                        Message.send(request, response, Message.RESULT.CUSTOM_CLASS_FORMAT_ERROR, null, "[" + type + "][" + id + "]");
                                                        return;
                                                }
                                                String packageName = id.split("\\.")[0];
                                                String className = id.split("\\.")[1];
                                                String methodName = id.split("\\.")[2];
                                                businessClass = Class.forName(packageName + "." + className);
                                                classMethod = businessClass.getMethod(methodName);
                                                Constructor<?> constructor = businessClass.getConstructor(paramsType);
                                                Object o = constructor.newInstance(params);
                                                Message m = (Message) classMethod.invoke(o);
                                                if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                        c.rollback();
                                                        Message.send(request, response, m.getResult(), null, m.getDetail());
                                                        return;
                                                }
                                        } catch (Exception e) {
                                                c.rollback();
                                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                                                Message.send(request, response, Message.RESULT.EXCEPTION, null, e.toString());
                                                return;
                                        }
                                } else if (type.toLowerCase().startsWith("foreach-custom<<")) {
                                        if (2 != type.split("<<").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_ALIAS_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        if (2 != type.split("<<")[1].split(":").length) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_PARAMETER_ERROR, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        // 结果集别名
                                        String aliasName = type.split("<<")[1].split(":")[0];
                                        ArrayList<HashMap<String, Object>> list = aliasNameMap.get(aliasName);
                                        if (null == list) {
                                                c.rollback();
                                                Message.send(request, response, Message.RESULT.RESULT_NOT_EXIST, null, "[" + type + "][" + id + "]");
                                                return;
                                        }
                                        Iterator<HashMap<String, Object>> iter = list.iterator();
                                        while (iter.hasNext()) {
                                                HashMap<String, Object> hm = iter.next();
                                                // 结果集引用数据的参数名
                                                String[] aliasParameterList = type.split(">>")[1].split(":")[1].split(",");
                                                for (int i = 0; i < aliasParameterList.length; i++) {
                                                        if (-1 == aliasParameterList[i].indexOf("->")) {
                                                                sqlParameter.put(aliasParameterList[i], hm.get(aliasParameterList[i]));
                                                        } else {
                                                                // 替换别名
                                                                String s = aliasParameterList[i].split("->")[1];
                                                                sqlParameter.put(s, hm.get(aliasParameterList[i]));
                                                        }
                                                }
                                                Object[] params = { this.httpServlet, this.request, this.response, c, sqlParameter };
                                                Class<?>[] paramsType = { HttpServlet.class, HttpServletRequest.class, HttpServletResponse.class, Connection.class, HashMap.class };
                                                try {
                                                        Class<?> businessClass = null;
                                                        Method classMethod = null;
                                                        if (3 != id.split("\\.").length) {
                                                                c.rollback();
                                                                Message.send(request, response, Message.RESULT.CUSTOM_CLASS_FORMAT_ERROR, null, "[" + type + "][" + id + "]");
                                                                return;
                                                        }
                                                        String packageName = id.split("\\.")[0];
                                                        String className = id.split("\\.")[1];
                                                        String methodName = id.split("\\.")[2];
                                                        businessClass = Class.forName(packageName + "." + className);
                                                        classMethod = businessClass.getMethod(methodName);
                                                        Constructor<?> constructor = businessClass.getConstructor(paramsType);
                                                        Object o = constructor.newInstance(params);
                                                        Message m = (Message) classMethod.invoke(o);
                                                        if (m.getResult() != Message.RESULT.SUCCESS) {// Success标记执行是否成功
                                                                c.rollback();
                                                                Message.send(request, response, m.getResult(), null, m.getDetail());
                                                                return;
                                                        }
                                                } catch (Exception e) {
                                                        c.rollback();
                                                        Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                                                        Message.send(request, response, Message.RESULT.EXCEPTION, null, e.toString());
                                                        return;
                                                }
                                        }
                                } else {
                                        c.rollback();
                                        Message.send(request, response, Message.RESULT.TRANSACTION_TYPE_ERROR, null, "[" + type + "][" + id + "]");
                                        return;
                                }
                                namespaceIndex++;
                        }
                        c.commit();
                        Message.send(request, response, Message.RESULT.SUCCESS, null, null);
                } catch (Exception e) {
                        try {
                                c.rollback();
                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                                Message.send(request, response, Message.RESULT.EXCEPTION, null, e.toString());
                        } catch (Exception e2) {
                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e2.toString());
                        }
                } finally {
                        try {
                                c.close();
                        } catch (Exception e) {
                                Framework.LOG.warn(SimpleDBO.MODULE_NAME, e.toString());
                        }
                }
        }

        @Override
        public DbInstanceModel getSqlModel(boolean autoCommit) {
                return DbFactory.getInstance();
        }
}
