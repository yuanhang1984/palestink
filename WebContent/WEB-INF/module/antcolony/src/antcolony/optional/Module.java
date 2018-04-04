package module.antcolony.optional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.json.JSONArray;
import org.json.JSONObject;
import library.io.InputOutput;
import framework.sdk.Framework;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.CustomAction;
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
         * 获取已加载模块
         */
        public Message getLoadNameList() {
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
         * 获取硬盘（全部）模块
         */
        public Message getDiskNameList() {
                Message msg = new Message();
                try {
                        JSONArray dataList = new JSONArray();
                        Iterator<String> iter = InputOutput.getCurrentDirectoryFolderName(Framework.PROJECT_REAL_PATH + "WEB-INF/module/").iterator();
                        while (iter.hasNext()) {
                                JSONObject obj = new JSONObject();
                                String name = iter.next();
                                obj.put("name", name);
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
         * 读取服务器资源文件
         * 由于文本内容过多，这里采用gzip的压缩方式，能够大幅减少传输占用的数据量。
         * 
         * [参数列表所需参数]
         * moduleName: 模块名称
         * fileName: 资源文件名称
         */
        public Message readServerResourceFile() {
                Message msg = new Message();
                GZIPOutputStream gos = null;
                PrintWriter pw = null;
                try {
                        gos = new GZIPOutputStream(this.httpServletResponse.getOutputStream());
                        pw = new PrintWriter(gos);
                        this.httpServletResponse.setHeader("Content-Encoding", "gzip");
                        StringBuilder sb = InputOutput.simpleStringBuilderReadFile(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/res/" + parameter.get("fileName"));
                        JSONObject o = new JSONObject();
                        o.put("status", Message.STATUS.SUCCESS);
                        o.put("error", Message.ERROR.NONE);
                        o.put("detail", sb.toString());
                        pw.write(o.toString());
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
                                if (null != pw) {
                                        pw.flush();
                                        pw.close();
                                }
                                if (null != gos) {
                                        gos.close();
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

        /**
         * 下载服务器的资源文件
         * 
         * [参数列表所需参数]
         * moduleName: 模块名称
         * fileName: 资源文件名称
         */
        public Message downloadServerResourceFile() {
                Message msg = new Message();
                InputStream is = null;
                OutputStream os = null;
                File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/res/" + parameter.get("fileName"));
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

        /**
         * 上传服务器的资源文件
         * 
         * [参数列表所需参数]
         * attachment: 上传的文件
         * moduleName: 模块名称
         * fileName: 资源文件的名称
         */
        public Message uploadServerResourceFile() {
                Message msg = new Message();
                try {
                        FileItem attachment = (FileItem) parameter.get("attachment");
                        if (null == attachment) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.PARAMETER_FORMAT_ERROR);
                                msg.setDetail("attachment");
                                return msg;
                        }
                        File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/res/" + parameter.get("fileName"));
                        attachment.write(f);
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
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