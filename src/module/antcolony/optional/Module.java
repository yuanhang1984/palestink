package module.antcolony.optional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import framework.sdk.Framework;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.CustomAction;
import library.io.InputOutput;
import module.antcolony.necessary.Config;

public class Module extends CustomAction {
        // private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;
        private HashMap<String, Object> parameter;
        // private Connection connection;

        public Module(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                // this.httpServletRequest = httpServletRequest;
                this.httpServletResponse = httpServletResponse;
                this.parameter = parameter;
                // this.connection = connection;
        }

        /**
         * 获取模块
         */
        public Message getNameList() {
                Message msg = new Message();
                try {
                        JSONArray dataList = new JSONArray();
                        Iterator<Entry<String, ArrayList<String>>> iter = Framework.MODULE_SERVLET_MAP.entrySet().iterator();
                        while (iter.hasNext()) {
                                Entry<String, ArrayList<String>> module = iter.next();
                                JSONObject obj = new JSONObject();
                                obj.put("name", module.getKey());
                                dataList.put(obj);
                        }
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        msg.setDetail(dataList);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * 获取权限
         */
        public Message getPermissionList() {
                Message msg = new Message();
                try {
                        JSONArray dataList = new JSONArray();
                        Iterator<Entry<String, ArrayList<String>>> iter = Framework.MODULE_SERVLET_MAP.entrySet().iterator();
                        while (iter.hasNext()) {
                                Entry<String, ArrayList<String>> module = iter.next();
                                JSONObject obj = new JSONObject();
                                JSONArray array = new JSONArray();
                                Iterator<String> servletIter = module.getValue().iterator();
                                while (servletIter.hasNext()) {
                                        JSONObject tmp = new JSONObject();
                                        String s = servletIter.next();
                                        tmp.put("name", s);
                                        tmp.put("value", module.getKey() + "." + s);
                                        array.put(tmp);
                                }
                                obj.put("module", module.getKey());
                                obj.put("servlet", array);
                                dataList.put(obj);
                        }
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        msg.setDetail(dataList);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * 获取模块的sql.xml文件内容
         * 
         * [参数列表所需参数]
         * name: 模块名称
         */
        public Message getSqlContent() {
                Message msg = new Message();
                try {
                        StringBuilder sb = InputOutput.readFileToStringBuilder(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("name") + "/res/sql.xml", 10240, "utf-8");
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        msg.setDetail(sb.toString());
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * 下载服务器的资源文件
         * 
         * [参数列表所需参数]
         * name: 模块名称
         * type: 资源类型
         *       sql sql.xml文件
         *       dispatch dispatch.xml文件
         */
        public Message downloadServerResourceFile() {
                Message msg = new Message();
                InputStream is = null;
                OutputStream os = null;
                String type = (String) parameter.get("type");
                String fileName = null;
                if (type.equalsIgnoreCase("sql")) {
                        fileName = "sql.xml";
                } else if (type.equalsIgnoreCase("dispatch")) {
                        fileName = "dispatch.xml";
                }
                File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("name") + "/res/" + fileName);
                try {
                        this.httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
                        is = new FileInputStream(f);
                        int size = 0;
                        byte[] buf = new byte[10240];
                        os = this.httpServletResponse.getOutputStream();
                        while (-1 != (size = is.read(buf))) {
                                try {
                                        os.write(buf, 0, size);
                                } catch (Exception e) {
                                        msg.setStatus(Message.STATUS.EXCEPTION);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail(e.toString());
                                        return msg;
                                }
                        }
                        msg.setSign(Message.SIGN.ALREADY_FEEDBACK_TO_CLIENT);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                } finally {
                        try {
                                if (null != is) {
                                        is.close();
                                }
                                if (null != os) {
                                        os.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail(e.toString());
                                return msg;
                        }
                }
        }
}