package module.user_security.optional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import framework.sdk.Framework;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.CustomAction;
import module.user_security.necessary.Config;

public class RolePermission extends CustomAction {
        // private HttpServletRequest httpServletRequest;
        // private HttpServletResponse httpServletResponse;
        // private HashMap<String, Object> parameter;
        // private Connection connection;

        public RolePermission(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                // this.httpServletRequest = httpServletRequest;
                // this.httpServletResponse = httpServletResponse;
                // this.parameter = parameter;
                // this.connection = connection;
        }

        /**
         * 获取模块及其权限
         */
        public Message getAllPermission() {
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
}