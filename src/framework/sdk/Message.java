package framework.sdk;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class Message {
        /*
         * 状态枚举代码
         */
        public static enum RESULT {
                SUCCESS/* 操作成功 */, ERROR/* 操作失败 */, EXCEPTION/* 操作异常 */, PARAM_INVALID/* 非法参数 */, PARAM_IS_NULL/* 参数为空 */, PARAM_FORMAT_ERROR/* 参数格式错误 */, PARAM_TRANSFORM_ERROR/* 参数转换错误 */, PARAM_HANDLE_EXCEPTION/* 参数处理异常 */, FILE_IS_NULL/* 文件为空 */, FILE_OVERSIZE/* 文件超出尺寸 */, FILE_SUFFIX_INVALID/* 非法文件后缀 */, FILE_UPLOAD_EXCEPTION/* 文件上传异常 */, MODULE_NO_PERMISSION/* 没有操作权限 */, MODULE_NO_DATA/* 没有数据 */, MODULE_DUPLICATE_DATA/* 重复数据 */, SDBO_TYPE_ERROR/* SDBO类型错误 */, COMPOSE_SQL_ERROR/* 组合SQL错误 */, UNKNOWN/* 未知错误 */
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
                case PARAM_INVALID:
                        return "Invalid Parameter";
                /* 参数为空 */
                case PARAM_IS_NULL:
                        return "Parameter Is Null";
                /* 参数格式错误 */
                case PARAM_FORMAT_ERROR:
                        return "Parameter Format Error";
                /* 参数转换错误 */
                case PARAM_TRANSFORM_ERROR:
                        return "Parameter Transform Error";
                /* 参数处理异常 */
                case PARAM_HANDLE_EXCEPTION:
                        return "Parameter Handle Exception";
                /* 文件为空 */
                case FILE_IS_NULL:
                        return "File Is Null";
                /* 文件超出尺寸 */
                case FILE_OVERSIZE:
                        return "File Oversize";
                /* 非法文件后缀 */
                case FILE_SUFFIX_INVALID:
                        return "Invalid File Suffix";
                /* 文件上传异常 */
                case FILE_UPLOAD_EXCEPTION:
                        return "File Upload Exception";
                /* 没有操作权限 */
                case MODULE_NO_PERMISSION:
                        return "No Permission";
                /* 没有数据 */
                case MODULE_NO_DATA:
                        return "No Data";
                /* 重复数据 */
                case MODULE_DUPLICATE_DATA:
                        return "Duplicate Data";
                /* SDBO类型错误 */
                case SDBO_TYPE_ERROR:
                        return "Sdbo Type Error";
                /* 组合SQL错误 */
                case COMPOSE_SQL_ERROR:
                        return "Compose Sql Error";
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
                        Framework.LOG.error(Framework.FRAMEWORK_MODULE_NAME, e.toString());
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