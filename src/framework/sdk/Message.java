package framework.sdk;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class Message {
        public static final String MODULE_NAME = "Message";

        private RESULT result;
        private Integer count;
        private String detail;
        private ArrayList<HashMap<String, Object>> dataList;// 用于存储所需的对象，比如select中返回的ArrayList。

        public Message() {
                this.result = null;
                this.count = null;
                this.detail = null;
                this.dataList = null;
        }

        public Message(RESULT result, Integer count, String detail, ArrayList<HashMap<String, Object>> dataList) {
                this.result = result;
                this.count = count;
                this.detail = detail;
                this.dataList = dataList;
        }

        public RESULT getResult() {
                return result;
        }

        public void setResult(RESULT result) {
                this.result = result;
        }

        public Integer getCount() {
                return count;
        }

        public void setCount(Integer count) {
                this.count = count;
        }

        public String getDetail() {
                return detail;
        }

        public void setDetail(String detail) {
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
        public static enum RESULT {
                SUCCESS/* 操作成功 */, ERROR/* 操作失败 */, EXCEPTION/* 操作异常 */, PARAMETER_INVALID/* 非法参数 */, PARAMETER_IS_NULL/* 参数为空 */, PARAMETER_FORMAT_ERROR/* 参数格式错误 */, PARAMETER_TRANSFORM_ERROR/* 参数转换错误 */, PARAMETER_HANDLE_EXCEPTION/* 参数处理异常 */, FILE_IS_NULL/* 文件为空 */, FILE_OVERSIZE/* 文件超出尺寸 */, FILE_SUFFIX_INVALID/* 非法文件后缀 */, FILE_UPLOAD_EXCEPTION/* 文件上传异常 */, NO_PERMISSION/* 没有权限 */, NO_MODULE_SQL/* 没有模块的SQL */, ANALYSE_NAMESPACE_ERROR/* 解析namespace错误 */, DATABASE_CONNECTION_ERROR/* 数据库连接错误 */, IDU_NO_DATA/* IDU操作无数据 */, IDU_EXIST_DATA/* IDU操作有结果 */, QUERY_NO_DATA/* 查询无数据 */, DUPLICATE_DATA/* 重复数据 */, TRANSACTION_TYPE_ERROR/* Transaction类型错误 */, RESULT_ALIAS_ERROR/* 结果集别名错误 */, RESULT_NOT_EXIST/* 结果集不存在 */, RESULT_PARAMETER_ERROR/* 结果集参数错误 */, COMPOSE_SQL_ERROR/* 组合SQL错误 */, RECURSIVE_SELECT_ERROR/* 递归检索错误 */, CUSTOM_CLASS_FORMAT_ERROR/* 自定义class格式错误 */, UNKNOWN/* 未知错误 */
        }

        /**
         * 将result转换成对应的字符串
         * 
         * @param result 状态
         * @return 对应的字符串
         */
        private static String transformStatus(RESULT result) {
                switch (result) {
                /* 操作成功 */
                case SUCCESS:
                        return "Success";
                /* 操作失败 */
                case ERROR:
                        return "Error";
                /* 操作异常 */
                case EXCEPTION:
                        return "Exception";
                /* 非法参数 */
                case PARAMETER_INVALID:
                        return "Parameter Invalid";
                /* 参数为空 */
                case PARAMETER_IS_NULL:
                        return "Parameter Is Null";
                /* 参数格式错误 */
                case PARAMETER_FORMAT_ERROR:
                        return "Parameter Format Error";
                /* 参数转换错误 */
                case PARAMETER_TRANSFORM_ERROR:
                        return "Parameter Transform Error";
                /* 参数处理异常 */
                case PARAMETER_HANDLE_EXCEPTION:
                        return "Parameter Handle Exception";
                /* 文件为空 */
                case FILE_IS_NULL:
                        return "File Is Null";
                /* 文件超出尺寸 */
                case FILE_OVERSIZE:
                        return "File Oversize";
                /* 非法文件后缀 */
                case FILE_SUFFIX_INVALID:
                        return "File Suffix Invalid";
                /* 文件上传异常 */
                case FILE_UPLOAD_EXCEPTION:
                        return "File Upload Exception";
                /* 没有操作权限 */
                case NO_PERMISSION:
                        return "No Permission";
                /* 没有模块的SQL */
                case NO_MODULE_SQL:
                        return "No Module Sql";
                /* 解析namespace错误 */
                case ANALYSE_NAMESPACE_ERROR:
                        return "Analyse Namespace Error";
                /* 数据库连接错误 */
                case DATABASE_CONNECTION_ERROR:
                        return "Database Connection Error";
                /* IDU无数据 */
                case IDU_NO_DATA:
                        return "IDU No Data";
                /* IDU操作有结果 */
                case IDU_EXIST_DATA:
                        return "IDU Exist Data";
                /* 查询无数据 */
                case QUERY_NO_DATA:
                        return "Query No Data";
                /* 重复数据 */
                case DUPLICATE_DATA:
                        return "Duplicate Data";
                /* Transaction类型错误 */
                case TRANSACTION_TYPE_ERROR:
                        return "Transaction Type Error";
                /* 结果集参数错误 */
                case RESULT_PARAMETER_ERROR:
                        return "Result Parameter Error";
                /* 结果集别名错误 */
                case RESULT_ALIAS_ERROR:
                        return "Result Alias Error";
                /* 结果集不存在 */
                case RESULT_NOT_EXIST:
                        return "Result Not Exist";
                /* 组合SQL错误 */
                case COMPOSE_SQL_ERROR:
                        return "Compose Sql Error";
                /* 递归检索错误 */
                case RECURSIVE_SELECT_ERROR:
                        return "Recursive Select Error";
                /* 自定义class格式错误 */
                case CUSTOM_CLASS_FORMAT_ERROR:
                        return "Custom Class Format Error";
                /* 未知错误 */
                default:
                        return "Unknown Error";
                }
        }

        /**
         * 向客户端发送消息
         * 
         * @param request HttpServletRequest对象
         * @param response HttpServletResponse对象
         * @param result 结果
         * @param count 数量（如果为null，则不体现）
         * @param detail 详情（如果为null，则不体现）
         */
        public static void send(HttpServletRequest request, HttpServletResponse response, RESULT result, Integer count, String detail) {
                try {
                        JSONObject o = new JSONObject();
                        if (RESULT.SUCCESS == result) {
                                o.put("status", 1);
                        } else {
                                o.put("status", 0);
                        }
                        o.put("result", transformStatus(result));
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