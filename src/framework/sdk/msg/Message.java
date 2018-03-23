package framework.sdk.msg;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import framework.sdk.Framework;

public class Message {
        public static final String MODULE_NAME = "Message";

        private STATUS status;
        private ERROR error;
        private SIGN sign;
        private Integer count;
        private Object detail;
        private ArrayList<HashMap<String, Object>> dataList;// 用于存储所需的对象，比如select中返回的ArrayList。

        public Message() {
                this.status = null;
                this.error = null;
                this.count = null;
                this.detail = null;
                this.dataList = null;
        }

        public STATUS getStatus() {
                return status;
        }

        public void setStatus(STATUS status) {
                this.status = status;
        }

        public ERROR getError() {
                return error;
        }

        public void setError(ERROR error) {
                this.error = error;
        }

        public SIGN getSign() {
                return sign;
        }

        public void setSign(SIGN sign) {
                this.sign = sign;
        }

        public Integer getCount() {
                return count;
        }

        public void setCount(Integer count) {
                this.count = count;
        }

        public Object getDetail() {
                return detail;
        }

        public void setDetail(Object detail) {
                this.detail = detail;
        }

        public ArrayList<HashMap<String, Object>> getDataList() {
                return dataList;
        }

        public void setDataList(ArrayList<HashMap<String, Object>> dataList) {
                this.dataList = dataList;
        }

        /*
         * 状态枚举代码
         */
        public static enum STATUS {
                // 操作成功
                SUCCESS,
                // 操作失败
                ERROR,
                // 操作异常
                EXCEPTION
        }

        /*
         * 错误枚举代码
         */
        public static enum ERROR {
                // 没有错误(用于标记SUCCESS的状态)
                NONE,
                // 没有Servlet的Name
                NO_SERVLET_NAME,
                // 参数为空
                PARAMETER_IS_NULL,
                // 参数格式错误
                PARAMETER_FORMAT_ERROR,
                // 参数转换错误
                PARAMETER_TRANSFORM_ERROR,
                // 参数处理异常
                PARAMETER_HANDLE_EXCEPTION,
                // 文件为空
                FILE_IS_NULL,
                // 文件超出尺寸
                FILE_OVERSIZE,
                // 非法文件后缀
                FILE_SUFFIX_INVALID,
                // 文件上传异常
                FILE_UPLOAD_EXCEPTION,
                // 没有权限
                NO_PERMISSION,
                // 没有模块的SQL
                NO_MODULE_SQL,
                // 解析namespace错误
                ANALYSE_NAMESPACE_ERROR,
                // 数据库连接错误
                DATABASE_CONNECTION_ERROR,
                // IDU操作无数据
                IDU_NO_DATA,
                // 查询无数据
                QUERY_NO_DATA,
                // 查询已有数据
                QUERY_EXIST_DATA,
                // Transaction类型错误
                TRANSACTION_TYPE_ERROR,
                // 结果集别名错误
                RESULT_ALIAS_ERROR,
                // 结果集不存在
                RESULT_NOT_EXIST,
                // 结果集参数错误
                RESULT_PARAMETER_ERROR,
                // 组合SQL错误
                COMPOSE_SQL_ERROR,
                // 递归检索错误
                RECURSIVE_SELECT_ERROR,
                // 自定义class格式错误
                CUSTOM_CLASS_FORMAT_ERROR,
                // 其他错误
                OTHER
        }

        /*
         * 标记枚举代码
         */
        public static enum SIGN {
                // 已经向客户端有了反馈
                ALREADY_FEEDBACK_TO_CLIENT
        }

        /**
         * 向客户端发送消息
         * 
         * @param request HttpServletRequest对象
         * @param response HttpServletResponse对象
         * @param status 状态
         * @param error 错误
         * @param count 数量（如果为null，则不体现）
         * @param detail 详情（如果为null，则不体现）
         *        注意，detail不能是String，这里需要考虑JSONArray对象的情况。如果强制转换成String，那么前端接收的时候就会多一对双引号。
         */
        public static void send(HttpServletRequest request, HttpServletResponse response, STATUS status, ERROR error, Integer count, Object detail) {
                try {
                        JSONObject o = new JSONObject();
                        o.put("status", status);
                        o.put("error", error);
                        if (null != count)
                                o.put("count", count);
                        if (null != detail)
                                o.put("detail", detail);
                        /*
                         * 接收参数如果包括callback参数（jsonp请求），需要特殊处理。
                         */
                        String callback = request.getParameter("callback");
                        if ((null != callback) && (callback.length() > 0)) {
                                Message.responseToClient(response, callback + "(" + o.toString() + ")");
                        } else {
                                Message.responseToClient(response, o.toString());
                        }
                } catch (Exception e) {
                        Framework.LOG.error(Message.MODULE_NAME, e.toString());
                }
        }

        /**
         * 向HttpServlet的客户端输出
         * 
         * @param response 待输出的response
         * @param msg 待输出的字符串数据
         */
        public static void responseToClient(HttpServletResponse response, String msg) throws Exception {
                PrintWriter pw = null;
                try {
                        pw = response.getWriter();
                        pw.write(msg);
                } finally {
                        pw.flush();
                        pw.close();
                }
        }
}