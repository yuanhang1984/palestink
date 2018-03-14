package framework.core;

import java.io.File;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
//commons-fileupload-1.3.2.jar
//commons-io-2.5.jar
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import library.bool.Bool;
import library.encrypt.Md5;
import library.string.CharacterString;
import framework.sdk.Framework;
import framework.sdk.msg.Message;
import framework.dispatch.object.Parameter;

@SuppressWarnings("serial")
public class Dispatch extends HttpServlet {
        public static final String MODULE_NAME = "Dispatch";
        /*
         * 模块的名称
         */
        // private String moduleName;

        /*
         * 配置文件路径
         */
        private String configFilePath;

        /*
         * 当前dispatch配置MAP
         */
        private HashMap<String, framework.dispatch.object.Servlet> dispatchParameterMap;

        /*
         * 全局dispatch配置MAP
         */
        // public static HashMap<String, framework.dispatch.object.Servlet> DISPATCH_PARAMETER_MAP = new HashMap<String, framework.dispatch.object.Servlet>();

        /*
         * 全局角色
         */
        private static HashMap<String, String> USER_ROLE_MAP = new HashMap<String, String>();

        /**
         * 构造函数
         */
        public Dispatch() {
                this.dispatchParameterMap = new HashMap<String, framework.dispatch.object.Servlet>();
        }

        /**
         * 初始化dispatch配置MAP（从配置文件读取）
         */
        private void initDispatchParameterMap() {
                try {
                        File file = new File(this.configFilePath);
                        SAXReader reader = new SAXReader();
                        Document doc = reader.read(file);
                        Element root = doc.getRootElement();
                        Element dispatch = root.element("dispatch");
                        Iterator<?> servletIter = dispatch.elements("servlet").iterator();
                        while (servletIter.hasNext()) {
                                Element servletElement = (Element) servletIter.next();
                                String name = servletElement.elementTextTrim("name");
                                String description = servletElement.elementTextTrim("description");
                                String namespace = servletElement.elementTextTrim("namespace").replaceAll("\\s", "");
                                String permission = servletElement.elementTextTrim("permission");
                                Element parameters = servletElement.element("parameters");
                                /*
                                 * 一个接口的“参数列表”可以为空，但需要设置其他“参数”。这里检查的是“是否存在<parameters>标签。
                                 */
                                if (null == parameters) {
                                        this.dispatchParameterMap.put(name, new framework.dispatch.object.Servlet(name, description, namespace, permission, null));
                                        // Dispatch.DISPATCH_PARAMETER_MAP.put(this.moduleName + "." + servletName, new framework.dispatch.object.Servlet(servletName, sdboType, namespace, permission, null));
                                        continue;
                                }
                                List<?> parameterList = parameters.elements("parameter");
                                /*
                                 * 一个接口“参数列表”可以为空，但需要设置其他“参数”。这里检查的是“是否存在<parameter>标签（与上面不同）。
                                 */
                                if ((null == parameterList) || (0 >= parameterList.size())) {
                                        this.dispatchParameterMap.put(name, new framework.dispatch.object.Servlet(name, description, namespace, permission, null));
                                        // Dispatch.DISPATCH_PARAMETER_MAP.put(this.moduleName + "." + servletName, new framework.dispatch.object.Servlet(servletName, sdboType, namespace, permission, null));
                                        continue;
                                }
                                Parameter[] list = new Parameter[parameterList.size()];
                                Iterator<?> parameterIter = parameterList.iterator();
                                int i = 0;
                                while (parameterIter.hasNext()) {
                                        Element e = (Element) parameterIter.next();
                                        String paramName = e.elementTextTrim("name");
                                        String paramDescription = e.elementTextTrim("description");
                                        String type = e.elementTextTrim("type");
                                        String format = e.elementTextTrim("format");
                                        String transform = e.elementTextTrim("transform");
                                        Object constant = e.elementTextTrim("constant");
                                        boolean allowNull = Boolean.parseBoolean(e.elementTextTrim("allowNull"));
                                        long fileMaxSize = 0;
                                        if (null != e.elementTextTrim("fileMaxSize")) {
                                                fileMaxSize = Long.parseLong(e.elementTextTrim("fileMaxSize"));
                                        }
                                        list[i] = new Parameter(paramName, paramDescription, type, format, transform, constant, allowNull, fileMaxSize);
                                        i++;
                                }
                                this.dispatchParameterMap.put(name, new framework.dispatch.object.Servlet(name, description, namespace, permission, list));
                                // Dispatch.DISPATCH_PARAMETER_MAP.put(this.moduleName + "." + servletName, new framework.dispatch.object.Servlet(servletName, sdboType, namespace, permission, list));
                        }
                } catch (Exception e) {
                        Framework.LOG.warn(Dispatch.MODULE_NAME, e.toString());
                }
        }

        /**
         * 检查当前连接的用户角色是否具有servlet的操作权限
         * 
         * @param request 当前连接下的request
         * @param response 当前连接下的response
         * @param servlet 与请求相对应的配置文件servlet
         * @return 如果拥有操作权限，返回true；如果没有操作权限，返回false。
         */
        private boolean checkServletPermission(HttpServletRequest request, HttpServletResponse response, framework.dispatch.object.Servlet servlet) {
                HttpSession hs = request.getSession();
                if (!servlet.getPermission().equalsIgnoreCase("none")) {
                        String roleStr = (String) hs.getAttribute(Framework.USER_ROLE);
                        if (null == roleStr) {
                                return false;
                        }
                        String permission_list = Dispatch.USER_ROLE_MAP.get(roleStr);
                        if (null == permission_list) {
                                return false;
                        }
                        if (permission_list.equalsIgnoreCase("*")) {
                                return true;
                        }
                        String[] arr = permission_list.split(";");
                        for (int i = 0; i < arr.length; i++) {
                                if (arr[i].equalsIgnoreCase(servlet.getPermission())) {
                                        return true;
                                }
                        }
                        return false;
                }
                return true;
        }

        /**
         * 处理文本参数
         * 
         * @param request 当前连接下的request
         * @param response 当前连接下的response
         * @param parameter 参数
         * @param isBroken 是否跳出Dispatch逻辑处理
         * @param paramList 目标参数列表
         * @return 对应的参数对象
         */
        private Object handleTextParameter(HttpServletRequest request, HttpServletResponse response, Parameter parameter, Bool isBroken, HashMap<String, Object> paramList) {
                /*
                 * 常量处理
                 */
                if (parameter.getFormat().equalsIgnoreCase("#constant#")) {
                        String constStr = (String) parameter.getConstant();
                        if (constStr.equalsIgnoreCase("#null")) {
                                return null;
                        } else if (constStr.equalsIgnoreCase("#now")) {
                                return new java.sql.Timestamp(System.currentTimeMillis());
                        } else if (constStr.equalsIgnoreCase("#uuid")) {
                                return CharacterString.getUuidStr(true);
                        } else if (constStr.equalsIgnoreCase("#session_user_uuid")) {
                                Object obj = request.getSession().getAttribute(Framework.USER_UUID);
                                if (null == obj) {
                                        Message.send(request, response, Message.RESULT.PARAMETER_IS_NULL, null, parameter.getName());
                                        isBroken.setBool(true);
                                        return null;
                                }
                                return (String) obj;
                        } else if (constStr.equalsIgnoreCase("#session_user_role")) {
                                Object obj = request.getSession().getAttribute(Framework.USER_ROLE);
                                if (null == obj) {
                                        Message.send(request, response, Message.RESULT.PARAMETER_IS_NULL, null, parameter.getName());
                                        isBroken.setBool(true);
                                        return null;
                                }
                                return (String) obj;
                        } else if (constStr.toLowerCase().startsWith("#timestamp")) {
                                String regex = "#timestamp\\[(.{1,})\\]";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(constStr);
                                if (matcher.find()) {
                                        return CharacterString.getCurrentFormatDateTime(matcher.group(1));
                                } else {
                                        return null;
                                }
                        } else {
                                try {
                                        if (parameter.getType().equalsIgnoreCase("integer")) {
                                                return Integer.getInteger(constStr);
                                        } else if (parameter.getType().equalsIgnoreCase("string")) {
                                                return constStr;
                                        } else if (parameter.getType().equalsIgnoreCase("double")) {
                                                return Double.valueOf(constStr);
                                        } else if (parameter.getType().equalsIgnoreCase("timestamp")) {
                                                return java.sql.Timestamp.valueOf(constStr);
                                        } else if (parameter.getType().equalsIgnoreCase("jsonobject")) {
                                                return new JSONObject(constStr).toString();
                                        } else if (parameter.getType().equalsIgnoreCase("jsonarray")) {
                                                return new JSONArray(constStr).toString();
                                        } else {
                                                Message.send(request, response, Message.RESULT.PARAMETER_TRANSFORM_ERROR, null, parameter.getName());
                                                isBroken.setBool(true);
                                                return null;
                                        }
                                } catch (Exception e) {
                                        /*
                                         * 通过异常判断参数类型转换是否成功
                                         */
                                        Message.send(request, response, Message.RESULT.PARAMETER_HANDLE_EXCEPTION, null, e.toString());
                                        isBroken.setBool(true);
                                        return null;
                                }
                        }
                }
                /*
                 * 参数处理
                 */
                String paramValue = request.getParameter(parameter.getName());
                /*
                 * 参数是否允许为空
                 */
                if (false == parameter.getAllowNull()) {
                        /*
                         * 如果不允许为空，但却是空。那么给出错误信息。
                         */
                        if (null == paramValue) {
                                Message.send(request, response, Message.RESULT.PARAMETER_IS_NULL, null, parameter.getName());
                                isBroken.setBool(true);
                                return null;
                        }
                }
                /*
                 * 不论参数是否设置为空，只要它有数据。那么就检查它的合法性。
                 */
                if (null != paramValue) {
                        /*
                         * 在不为空的状况下，用正则表达式检查参数合法性
                         */
                        if (false == CharacterString.regularExpressionCheck(parameter.getFormat(), paramValue)) {
                                Message.send(request, response, Message.RESULT.PARAMETER_FORMAT_ERROR, null, parameter.getName());
                                isBroken.setBool(true);
                                return null;
                        }
                        /*
                         * 数据转换
                         */
                        if (null != parameter.getTransform()) {
                                if (parameter.getTransform().equalsIgnoreCase("#encrypt_md5")) {
                                        try {
                                                paramValue = Md5.encode(paramValue.getBytes("utf-8"));
                                        } catch (Exception e) {
                                                Message.send(request, response, Message.RESULT.PARAMETER_TRANSFORM_ERROR, null, e.toString());
                                                isBroken.setBool(true);
                                                return null;
                                        }
                                }
                        }
                        try {
                                if (parameter.getType().equalsIgnoreCase("integer")) {
                                        return Integer.getInteger(paramValue);
                                } else if (parameter.getType().equalsIgnoreCase("string")) {
                                        return paramValue;
                                } else if (parameter.getType().equalsIgnoreCase("double")) {
                                        return Double.valueOf(paramValue);
                                } else if (parameter.getType().equalsIgnoreCase("timestamp")) {
                                        return java.sql.Timestamp.valueOf(paramValue);
                                } else if (parameter.getType().equalsIgnoreCase("jsonobject")) {
                                        return new JSONObject(paramValue).toString();
                                } else if (parameter.getType().equalsIgnoreCase("jsonarray")) {
                                        return new JSONArray(paramValue).toString();
                                } else {
                                        Message.send(request, response, Message.RESULT.PARAMETER_TRANSFORM_ERROR, null, parameter.getName());
                                        isBroken.setBool(true);
                                        return null;
                                }
                        } catch (Exception e) {
                                /*
                                 * 通过异常判断参数类型转换是否成功
                                 */
                                Message.send(request, response, Message.RESULT.PARAMETER_HANDLE_EXCEPTION, null, e.toString());
                                isBroken.setBool(true);
                                return null;
                        }
                }
                /*
                 * 这种情况是，参数允许为空，且为空。
                 */
                return null;
        }

        /**
         * 处理文件参数
         * 
         * @param request 当前连接下的request
         * @param response 当前连接下的response
         * @param parameter 参数
         * @return 对应的文件对象
         */
        private FileItem handleFileParameter(HttpServletRequest request, HttpServletResponse response, Parameter parameter, Bool isBroken) {
                DiskFileItemFactory dfif = new DiskFileItemFactory();
                ServletFileUpload sfu = new ServletFileUpload(dfif);
                /*
                 * 单个文件上传最大限制，利用属性限制尺寸，可以只接收文件一部分时，根据文件头判断文件的大小。
                 */
                sfu.setFileSizeMax(parameter.getFileMaxSize());
                boolean noFile = true;
                FileItem fi = null;
                try {
                        Iterator<FileItem> iter = sfu.parseRequest(request).iterator();
                        /*
                         * 单次只允许上传一个文件（这里用if和return null）。
                         */
                        if (iter.hasNext()) {
                                noFile = false;
                                fi = iter.next();
                                /*
                                 * 这里对文件尺寸的大小判断，基于文件全部上传后才行。
                                 */
                                if (fi.getSize() >= parameter.getFileMaxSize()) {
                                        Message.send(request, response, Message.RESULT.FILE_OVERSIZE, null, null);
                                        isBroken.setBool(true);
                                        return null;
                                }
                                if (false == CharacterString.regularExpressionCheck(parameter.getFormat(), fi.getName().toLowerCase())) {
                                        Message.send(request, response, Message.RESULT.FILE_SUFFIX_INVALID, null, null);
                                        isBroken.setBool(true);
                                        return null;
                                }
                        }
                        if (true == noFile) {
                                if (false == parameter.getAllowNull()) {
                                        Message.send(request, response, Message.RESULT.FILE_IS_NULL, null, null);
                                        isBroken.setBool(true);
                                        return null;
                                }
                        }
                        return fi;
                } catch (FileSizeLimitExceededException e) {
                        /*
                         * 利用FileSizeLimitExceededException异常限制文件上传的大小
                         */
                        Message.send(request, response, Message.RESULT.FILE_OVERSIZE, null, null);
                        isBroken.setBool(true);
                        return null;
                } catch (FileUploadException e) {
                        Message.send(request, response, Message.RESULT.FILE_UPLOAD_EXCEPTION, null, e.toString());
                        isBroken.setBool(true);
                        return null;
                } catch (Exception e) {
                        Message.send(request, response, Message.RESULT.EXCEPTION, null, e.toString());
                        isBroken.setBool(true);
                        return null;
                }
        }

        /**
         * 初始化<br />
         * 从参数读取配置文件信息
         */
        @Override
        public void init() throws ServletException {
                super.init();
                // this.moduleName = this.getInitParameter("moduleName");
                this.configFilePath = this.getInitParameter("path");
                this.initDispatchParameterMap();
                // Framework.LOG.info(Dispatch.MODULE_NAME, "[" + this.moduleName + "] Load Complete");
        }

        /**
         * 方便测试
         */
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) {
                doPost(request, response);
        }

        /**
         * doPost
         */
        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) {
                /*
                 * 接收servletName参数
                 */
                String name = null;
                String uri = request.getRequestURI();
                Pattern pattern = Pattern.compile("(.+?)\\/");
                Matcher matcher = pattern.matcher(uri);
                // 循环遍历语句中的每个变量
                while (matcher.find()) {
                        name = matcher.group(1);
                }
                if ((null == name) || (0 >= name.length())) {
                        Message.send(request, response, Message.RESULT.NO_SERVLET_NAME, null, null);
                        return;
                }
                /*
                 * 判断servletName参数的合法性
                 */
                framework.dispatch.object.Servlet s = this.dispatchParameterMap.get(name);
                if (null == s) {
                        Message.send(request, response, Message.RESULT.NO_SERVLET_NAME, null, name);
                        return;
                }
                /*
                 * 检查操作权限
                 */
                if (false == this.checkServletPermission(request, response, s)) {
                        Message.send(request, response, Message.RESULT.NO_PERMISSION, null, name);
                        return;
                }
                /*
                 * 根据参数类型，选择“文本处理”还是“文件处理”。
                 */
                HashMap<String, Object> parameter = new HashMap<String, Object>();
                Parameter arr[] = s.getParameterList();
                if (null != arr) {
                        for (int i = 0; i < arr.length; i++) {
                                Bool isBroken = new Bool(false);
                                Object param = null;
                                Parameter p = arr[i];
                                if (p.getType().equalsIgnoreCase("file")) {
                                        param = this.handleFileParameter(request, response, p, isBroken);
                                } else {
                                        param = this.handleTextParameter(request, response, p, isBroken, parameter);
                                }
                                /*
                                 * 如果标记为true，跳出逻辑处理。
                                 */
                                if (isBroken.isBool()) {
                                        return;
                                }
                                if (null != param) {
                                        parameter.put(p.getName(), param);
                                }
                        }
                }
                /*
                 * sdbo操作
                 */
                SimpleDBO sdbo = new SimpleDBO(this, s.getNamespace(), parameter, request, response);
                sdbo.transaction();
        }
}