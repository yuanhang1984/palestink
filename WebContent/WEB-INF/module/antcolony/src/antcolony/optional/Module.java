package module.antcolony.optional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import library.execute.Run;
import library.io.InputOutput;
import library.string.CharacterString;
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
         * 获取当前类的参数
         */
        public HashMap<String, Object> getParameter() {
                return this.parameter;
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
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
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
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
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
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         * 读取服务器资源文件
         * 由于文本内容过多，这里采用gzip的压缩方式，能够大幅减少传输占用的数据量。
         * 
         * [参数列表所需参数]
         * directory: 文件所在目录(lib, res, src)
         * moduleName: 模块名称
         * fileName: 资源文件名称
         */
        public Message readServerFile() {
                Message msg = new Message();
                GZIPOutputStream gos = null;
                PrintWriter pw = null;
                try {
                        gos = new GZIPOutputStream(this.httpServletResponse.getOutputStream());
                        pw = new PrintWriter(gos);
                        this.httpServletResponse.setHeader("Content-Encoding", "gzip");
                        StringBuilder sb = InputOutput.simpleStringBuilderReadFile(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/" + parameter.get("directory") + "/" + parameter.get("fileName"));
                        JSONObject o = new JSONObject();
                        o.put("status", Message.STATUS.SUCCESS);
                        o.put("error", Message.ERROR.NONE);
                        o.put("detail", sb.toString());
                        pw.write(o.toString());
                        msg.setSign(Message.SIGN.ALREADY_FEEDBACK_TO_CLIENT);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
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
                                Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail(CharacterString.getExceptionStackTrace(e));
                                return msg;
                        }
                }
        }

        /**
        * 删除服务器的源码文件
        *
        * [参数列表所需参数]
        * directory: 文件所在目录(lib, res, src)
        * moduleName: 模块名称
        * fileName: 资源文件名称
        */
        public Message removeServerFile() {
                Message msg = new Message();
                try {
                        File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/" + parameter.get("directory") + "/" + parameter.get("fileName"));
                        if (!f.exists()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("File Is Not Exist");
                                return msg;
                        }
                        if (!f.delete()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("File Remove Error");
                                return msg;
                        }
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         * 下载服务器的资源文件
         * 
         * [参数列表所需参数]
         * directory: 文件所在目录(lib, res, src)
         * moduleName: 模块名称
         * fileName: 资源文件名称
         */
        public Message downloadServerFile() {
                Message msg = new Message();
                InputStream is = null;
                OutputStream os = null;
                File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/" + parameter.get("directory") + "/" + parameter.get("fileName"));
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
                                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                                        return msg;
                                }
                        }
                        msg.setSign(Message.SIGN.ALREADY_FEEDBACK_TO_CLIENT);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
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
                                Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail(CharacterString.getExceptionStackTrace(e));
                                return msg;
                        }
                }
        }

        /**
         * 下载Lib文件
         * 
         * [参数列表所需参数]
         * fileName: 资源文件名称（常量）
         */
        public Message downloadLib() {
                Message msg = new Message();
                InputStream is = null;
                OutputStream os = null;
                File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/lib/" + parameter.get("fileName"));
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
                                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                                        return msg;
                                }
                        }
                        msg.setSign(Message.SIGN.ALREADY_FEEDBACK_TO_CLIENT);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
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
                                Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail(CharacterString.getExceptionStackTrace(e));
                                return msg;
                        }
                }
        }

        /**
         * 上传服务器的资源文件
         * 
         * [参数列表所需参数]
         * attachment: 上传的文件
         * directory: 文件所在目录(lib, res, src)
         * moduleName: 模块名称
         * fileName: 资源文件的名称
         */
        public Message uploadServerFile() {
                Message msg = new Message();
                try {
                        FileItem attachment = (FileItem) parameter.get("attachment");
                        if (null == attachment) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.PARAMETER_FORMAT_ERROR);
                                msg.setDetail("attachment");
                                return msg;
                        }
                        File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/" + parameter.get("directory") + "/" + parameter.get("fileName"));
                        attachment.write(f);
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         * 生成config.xml文件的xml结构
         * 
         * @param moduleName 模块名称
         * @return config.xml文件的xml结构
         */
        private Document generateConfigXml(String moduleName) {
                Document doc = DocumentHelper.createDocument();
                doc.addDocType("xml", null, null);
                Element config = doc.addElement("Config");
                config.addComment("[SourceCode]");
                config.addComment("    rebuild");
                config.addComment("        0 不编译");
                config.addComment("        1 编译");
                config.addComment("    command: 编译源码的命令.多个jar和java之间用\";\"分隔.");
                Element sourceCode = config.addElement("SourceCode");
                sourceCode.addAttribute("rebuild", "1");
                sourceCode.addAttribute("command", "javac -g -encoding utf-8 -cp ${WEB_APP}/WEB-INF/lib/*;${WEB_APP}/WEB-INF/classes -d ${WEB_APP}/WEB-INF/module/" + moduleName + "/bin ${WEB_APP}/WEB-INF/module/" + moduleName + "/src/" + moduleName + "/necessary/Config.java ${WEB_APP}/WEB-INF/module/" + moduleName + "/src/" + moduleName + "/necessary/Custom.java ${WEB_APP}/WEB-INF/module/" + moduleName + "/src/" + moduleName + "/necessary/Daemon.java");
                config.addComment("[Module]");
                config.addComment("    enable");
                config.addComment("        true 启用模块");
                config.addComment("        false 禁用模块");
                Element module = config.addElement("Module");
                module.addAttribute("enable", "true");
                config.addComment("[Docs]");
                config.addComment("    enable");
                config.addComment("        true 启用API");
                config.addComment("        false 禁用API");
                Element docs = config.addElement("Docs");
                docs.addAttribute("enable", "true");
                config.addComment("请在下方配置自定义所需常量");
                return doc;
        }

        /**
         * 生成sql.xml文件的xml结构
         * 
         * @return sql.xml文件的xml结构
         */
        private Document generateSqlXml() {
                Document doc = DocumentHelper.createDocument();
                doc.addDocType("xml", null, null);
                doc.addElement("Sql");
                return doc;
        }

        /**
         * 生成dispatch.xml文件的xml结构
         * 
         * @return dispatch.xml文件的xml结构
         */
        private Document generateDispatchXml() {
                Document doc = DocumentHelper.createDocument();
                doc.addDocType("xml", null, null);
                Element config = doc.addElement("Config");
                config.addElement("dispatch");
                return doc;
        }

        /**
         * 获取xml的格式
         * @return 返回xml的格式
         */
        private OutputFormat generateXmlFormat() {
                OutputFormat fmt = new OutputFormat();
                // 编码
                fmt.setEncoding("utf-8");
                // 是否换行
                fmt.setNewlines(true);
                // 是否缩进
                fmt.setIndent(true);
                // 缩进代替符（4个空格）
                fmt.setIndent("    ");
                return fmt;
        }

        /**
         * 创建模块
         * 
         * [参数列表所需参数]
         * moduleName: 模块名称
         */
        public Message addModule() {
                Message msg = new Message();
                FileWriter fw = null;
                XMLWriter xw = null;
                try {
                        // 检查模块目录是否存在
                        File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName"));
                        if (f.exists()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module Name Is Exist");
                                return msg;
                        }
                        // 创建模块目录
                        if (!f.mkdirs()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module Create Error");
                                return msg;
                        }
                        // 创建模块目录下bin目录
                        f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/bin");
                        if (!f.mkdirs()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module bin Create Error");
                                return msg;
                        }
                        // 创建模块目录下lib目录
                        f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/lib");
                        if (!f.mkdirs()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module lib Create Error");
                                return msg;
                        }
                        // 创建模块目录下res目录
                        f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/res");
                        if (!f.mkdirs()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module res Create Error");
                                return msg;
                        }
                        // 创建模块目录下src目录
                        f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/src");
                        if (!f.mkdirs()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module src Create Error");
                                return msg;
                        }
                        // 创建模块目录下res/config.xml文件
                        f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/res/config.xml");
                        fw = new FileWriter(f);
                        xw = new XMLWriter(fw, this.generateXmlFormat());
                        xw.write(this.generateConfigXml((String) parameter.get("moduleName")));
                        xw.flush();
                        xw.close();
                        fw.close();
                        // 创建模块目录下res/sql.xml文件
                        f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/res/sql.xml");
                        fw = new FileWriter(f);
                        xw = new XMLWriter(fw, this.generateXmlFormat());
                        xw.write(this.generateSqlXml());
                        xw.flush();
                        xw.close();
                        fw.close();
                        // 创建模块目录下res/dispatch.xml文件
                        f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/res/dispatch.xml");
                        fw = new FileWriter(f);
                        xw = new XMLWriter(fw, this.generateXmlFormat());
                        xw.write(this.generateDispatchXml());
                        xw.flush();
                        xw.close();
                        fw.close();
                        // 返回结果
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                } finally {
                        try {
                                if (null != xw) {
                                        xw.close();
                                }
                                if (null != fw) {
                                        fw.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail(CharacterString.getExceptionStackTrace(e));
                                return msg;
                        }
                }
        }

        /**
         *  删除模块
         * 
         * [参数列表所需参数]
         * moduleName: 模块名称
         */
        public Message removeModule() {
                Message msg = new Message();
                try {
                        // 检查模块目录是否存在
                        File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName"));
                        if (!f.exists()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module Name Is Not Exist");
                                return msg;
                        }
                        // 遍历删除
                        InputOutput.clearDir(f);
                        // 检查路径下是否还有文件存在
                        ArrayList<String> list = InputOutput.getCurrentDirectoryAllFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName"), null);
                        if (null == list) {
                                msg.setStatus(Message.STATUS.SUCCESS);
                                msg.setError(Message.ERROR.NONE);
                                return msg;
                        }
                        if (0 >= list.size()) {
                                msg.setStatus(Message.STATUS.SUCCESS);
                                msg.setError(Message.ERROR.NONE);
                                return msg;
                        }
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail("Module Remove Error");
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         *  修改模块名称
         * 
         * [参数列表所需参数]
         * oldModuleName: 模块名称
         * newModuleName: 模块名称
         */
        public Message modifyModuleName() {
                Message msg = new Message();
                try {
                        // 检查模块目录是否存在
                        File oldFile = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("oldModuleName"));
                        if (!oldFile.exists()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module Name Is Not Exist");
                                return msg;
                        }
                        // 检查模块的新名称是否已存在
                        File newFile = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("newModuleName"));
                        if (newFile.exists()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module New Name Is Exist");
                                return msg;
                        }
                        if (!oldFile.renameTo(newFile)) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Module Name Modify Error");
                                return msg;
                        }
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         *  启动web服务
         */
        public Message startWebServer() {
                Message msg = new Message();
                try {
                        if (0 >= Config.START_COMMAND.length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Web Server Start Command Is Empty");
                                return msg;
                        }
                        Run.executeProgram(Config.START_COMMAND, null, null, false);
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         *  停止web服务
         */
        public Message stopWebServer() {
                Message msg = new Message();
                try {
                        if (0 >= Config.STOP_COMMAND.length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Web Server Stop Command Is Empty");
                                return msg;
                        }
                        Run.executeProgram(Config.START_COMMAND, null, null, false);
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                }
        }

        /**
         * 读取服务器日志文件
         * 由于文本内容过多，这里采用gzip的压缩方式，能够大幅减少传输占用的数据量。
         * 
         * [参数列表所需参数]
         * seek: 读取日志的开始点
         * number: 读取行数
         * 
         * [注意]
         * 返回数据中的seek是读取结束后的偏移，可用作下次读取的开始。
         */
        public Message readServerLogFile() {
                Message msg = new Message();
                GZIPOutputStream gos = null;
                PrintWriter pw = null;
                File f = null;
                RandomAccessFile raf = null;
                try {
                        if (0 >= Config.LOG_FILE_PATH.length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Log File Path Is Empty");
                                return msg;
                        }
                        f = new File(Config.LOG_FILE_PATH);
                        if (!f.exists()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("Log File Is Not Exist");
                                return msg;
                        }
                        raf = new RandomAccessFile(f, "r");
                        raf.seek((Long) parameter.get("seek"));
                        StringBuilder sb = new StringBuilder();
                        String s = "";
                        int i = 0;
                        boolean endOfFile = false;
                        Long seek = -1L;
                        while ((s = raf.readLine()) != null) {
                                sb.append(s);
                                sb.append(System.getProperty("line.separator"));
                                i++;
                                if (i >= (Integer) parameter.get("number")) {
                                        endOfFile = true;
                                        seek = raf.getFilePointer();
                                        break;
                                }
                        }
                        if (!endOfFile) {
                                seek = raf.getFilePointer();
                        }
                        JSONObject detail = new JSONObject();
                        detail.put("seek", seek);
                        detail.put("content", sb.toString());
                        gos = new GZIPOutputStream(this.httpServletResponse.getOutputStream());
                        pw = new PrintWriter(gos);
                        this.httpServletResponse.setHeader("Content-Encoding", "gzip");
                        JSONObject o = new JSONObject();
                        o.put("status", Message.STATUS.SUCCESS);
                        o.put("error", Message.ERROR.NONE);
                        o.put("detail", detail);
                        pw.write(o.toString());
                        msg.setSign(Message.SIGN.ALREADY_FEEDBACK_TO_CLIENT);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(CharacterString.getExceptionStackTrace(e));
                        return msg;
                } finally {
                        try {
                                if (null != raf) {
                                        raf.close();
                                }
                                if (null != pw) {
                                        pw.flush();
                                        pw.close();
                                }
                                if (null != gos) {
                                        gos.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.warn(Config.MODULE_NAME, CharacterString.getExceptionStackTrace(e));
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail(CharacterString.getExceptionStackTrace(e));
                                return msg;
                        }
                }
        }

        /**
         * 获取服务器源码文件的列表
         * 
         * [参数列表所需参数]
         * moduleName: 模块名称
         */
        public Message getServerSourceFileList() {
                Message msg = new Message();
                JSONObject ssfObj = new JSONObject();
                JSONArray necessaryArr = new JSONArray();
                JSONArray optionalArr = new JSONArray();
                // necessary
                ArrayList<String> necessaryList = InputOutput.getCurrentDirectoryFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/src/" + parameter.get("moduleName") + "/necessary", null);
                if (null != necessaryList) {
                        Iterator<String> iter = necessaryList.iterator();
                        while (iter.hasNext()) {
                                String path = iter.next();
                                File f = new File(path);
                                JSONObject necessaryObj = new JSONObject();
                                necessaryObj.put("name", f.getName());
                                necessaryArr.put(necessaryObj);
                        }
                }
                ssfObj.put("necessary", necessaryArr);
                // optional
                ArrayList<String> optionalList = InputOutput.getCurrentDirectoryFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/src/" + parameter.get("moduleName") + "/optional", null);
                if (null != optionalList) {
                        Iterator<String> iter = optionalList.iterator();
                        while (iter.hasNext()) {
                                String path = iter.next();
                                File f = new File(path);
                                JSONObject optionalObj = new JSONObject();
                                optionalObj.put("name", f.getName());
                                optionalArr.put(optionalObj);
                        }
                }
                ssfObj.put("optional", optionalArr);
                msg.setStatus(Message.STATUS.SUCCESS);
                msg.setError(Message.ERROR.NONE);
                msg.setDetail(ssfObj);
                return msg;
        }

        /**
         * 获取服务器lib文件的列表
         * 
         * [参数列表所需参数]
         * moduleName: 模块名称
         */
        public Message getServerLibraryFileList() {
                Message msg = new Message();
                JSONArray arr = new JSONArray();
                ArrayList<String> list = InputOutput.getCurrentDirectoryFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + parameter.get("moduleName") + "/lib", null);
                if (null != list) {
                        Iterator<String> iter = list.iterator();
                        while (iter.hasNext()) {
                                String path = iter.next();
                                File f = new File(path);
                                JSONObject o = new JSONObject();
                                o.put("name", f.getName());
                                arr.put(o);
                        }
                }
                msg.setStatus(Message.STATUS.SUCCESS);
                msg.setError(Message.ERROR.NONE);
                msg.setDetail(arr);
                return msg;
        }
}