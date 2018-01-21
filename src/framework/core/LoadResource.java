package framework.core;

import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import library.execute.Run;
import library.io.InputOutput;
import library.system.SystemKit;
import framework.sdk.Framework;
import framework.sdk.spec.module.necessary.DaemonAction;
import framework.ext.factory.DbFactory;
import framework.ext.factory.LogFactory;
import framework.sdbo.object.SqlRepository;

import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class LoadResource implements ServletContextListener {
        public static final String MODULE_NAME = "LoadResource";

        public LoadResource() {
                Framework.MODULE_NAME_LIST = new ArrayList<String>();
        }

        /**
         * 清空WEB-INF/classes文件夹下所有数据
         */
        private void clearWICDir() {
                File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/classes");
                if (!f.exists()) {
                        f.mkdirs();
                } else {
                        InputOutput.clearDir(f);
                }
        }

        /**
         * 加载Log配置
         * @param path log配置文件路径
         * @return 成功返回true;失败返回false.
         */
        private boolean loadLogConfig(String path) {
                try {
                        File file = new File(path);
                        if (!file.exists())
                                return false;
                        SAXReader reader = new SAXReader();
                        Document doc = reader.read(file);
                        Element root = doc.getRootElement();
                        Element sourceCode = root.element("SourceCode");
                        Framework.LOG_SOURCE_CODE_REBUILD = Integer.parseInt(sourceCode.attributeValue("rebuild"));
                        Framework.LOG_SOURCE_CODE_COMMAND = sourceCode.attributeValue("command").replaceAll("\\$\\{WEB_APP\\}/", Framework.PROJECT_REAL_PATH);
                        if (!SystemKit.isWindows()) {
                                // windows系统下文件分隔用分号
                                Framework.LOG_SOURCE_CODE_COMMAND = Framework.LOG_SOURCE_CODE_COMMAND.replaceAll(";", ":");
                        }
                        Element logFile = root.element("LogFile");
                        Framework.LOG_FILE_ENABLE = Boolean.parseBoolean(logFile.attributeValue("enable"));
                        Framework.LOG_FILE_LOG_PATH = logFile.attributeValue("logPath").replaceAll("\\$\\{WEB_APP\\}/", Framework.PROJECT_REAL_PATH);
                        Framework.LOG_FILE_ZIP_PATH = logFile.attributeValue("zipPath").replaceAll("\\$\\{WEB_APP\\}/", Framework.PROJECT_REAL_PATH);
                        Framework.LOG_FILE_SIZE = Integer.parseInt(logFile.attributeValue("size"));
                        Framework.LOG_FILE_FORMAT = logFile.attributeValue("format");
                } catch (Exception e) {
                        System.err.println(e);
                        return false;
                }
                return true;
        }

        /**
         * 加载Db配置
         * @param path db配置文件路径
         * @return 成功返回true;失败返回false.
         */
        private boolean loadDbConfig(String path) {
                try {
                        File file = new File(path);
                        if (!file.exists())
                                return false;
                        SAXReader reader = new SAXReader();
                        Document doc = reader.read(file);
                        Element root = doc.getRootElement();
                        Element sourceCode = root.element("SourceCode");
                        Framework.DB_SOURCE_CODE_REBUILD = Integer.parseInt(sourceCode.attributeValue("rebuild"));
                        Framework.DB_SOURCE_CODE_COMMAND = sourceCode.attributeValue("command").replaceAll("\\$\\{WEB_APP\\}/", Framework.PROJECT_REAL_PATH);
                        if (!SystemKit.isWindows()) {
                                // windows系统下文件分隔用分号
                                Framework.DB_SOURCE_CODE_COMMAND = Framework.DB_SOURCE_CODE_COMMAND.replaceAll(";", ":");
                        }
                        Element databaseInformation = root.element("DatabaseInformation");
                        Framework.DB_INFO_DRIVER = databaseInformation.attributeValue("driver");
                        Framework.DB_INFO_URL = databaseInformation.attributeValue("url");
                        Element databaseSecurity = root.element("DatabaseSecurity");
                        Framework.DB_SECURITY_NAME = databaseSecurity.attributeValue("name");
                        Framework.DB_SECURITY_PASSWORD = databaseSecurity.attributeValue("password");
                        Element databasePool = root.element("DatabasePool");
                        Framework.DB_POOL_MAXACTIVECONNECTION = Integer.parseInt(databasePool.attributeValue("maxActiveConnection"));
                } catch (Exception e) {
                        System.err.println(e);
                        return false;
                }
                return true;
        }

        @Override
        public void contextInitialized(ServletContextEvent sce) {
                /************************************************
                 * 初始化前的准备工作
                 ************************************************/
                // 初始化Framework的所需路径
                Framework.PROJECT_REAL_PATH = InputOutput.regulatePath(sce.getServletContext().getRealPath("/"));
                // 清空classes文件夹下所有文件
                this.clearWICDir();
                // 解压ext依赖jar至WEB-INF/classes目录
                ArrayList<String> jarList = InputOutput.getCurrentDirectoryAllFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/", ".jar");
                if (null != jarList) {
                        Iterator<String> jarIter = jarList.iterator();
                        while (jarIter.hasNext()) {
                                String p = jarIter.next();
                                File f = new File(p);
                                if (f.isFile()) {
                                        try {
                                                InputOutput.decompressDirectoryToJarFile(f.getAbsolutePath(), Framework.PROJECT_REAL_PATH + "WEB-INF/classes/");
                                        } catch (Exception e) {
                                                throw new RuntimeException("Decompress Ext Jar File Error: " + System.getProperty("line.separator") + e.toString());
                                        }
                                }
                        }
                }
                /************************************************
                 * 初始化Log
                 ************************************************/
                // 判断加载log配置是否成功
                if (!this.loadLogConfig(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/log/res/config.xml")) {
                        throw new RuntimeException("Load Log Config Error");
                }
                // 判断是否编译Log源码
                if (1 == Framework.LOG_SOURCE_CODE_REBUILD) {
                        // 编译Log源码
                        StringBuilder stderr = new StringBuilder();
                        try {
                                Run.executeProgram(Framework.LOG_SOURCE_CODE_COMMAND, null, stderr, true);
                        } catch (Exception e) {
                                throw new RuntimeException("Run Log Source Code Command Error: " + System.getProperty("line.separator") + e.toString());
                        }
                        if (stderr.length() > 1) {
                                throw new RuntimeException("Complie Log Source Code Command Error: " + System.getProperty("line.separator") + stderr);
                        } else {
                                System.out.println("Log Source Complie Complete.");
                        }
                }
                try {
                        // 将编译好的Log文件，复制到classes目录
                        InputOutput.copyDirectory(InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/log/bin"), InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/classes"));
                } catch (Exception e) {
                        throw new RuntimeException("Copy Log Class Error: " + System.getProperty("line.separator") + e.toString());
                }
                if (Framework.LOG_FILE_ENABLE) {
                        // 创建log输出文件所需的目录
                        File logDirPath = new File(Framework.LOG_FILE_LOG_PATH);
                        if (!logDirPath.exists()) {
                                logDirPath.mkdirs();
                        }
                        logDirPath = new File(Framework.LOG_FILE_ZIP_PATH);
                        if (!logDirPath.exists()) {
                                logDirPath.mkdirs();
                        }
                }
                // 根据LogFactory初始化Framework的Log对象
                Framework.LOG = new LogFactory("record.log", InputOutput.regulatePath(Framework.LOG_FILE_LOG_PATH), InputOutput.regulatePath(Framework.LOG_FILE_ZIP_PATH), Framework.LOG_FILE_SIZE);
                if (!Framework.LOG.init()) {
                        throw new RuntimeException("LogFactory Initialize Error");
                }
                Framework.LOG.info(LoadResource.MODULE_NAME, "The [Log Module] Initialization Is Complete");
                /************************************************
                 * 初始化Db
                 ************************************************/
                // 判断加载db配置是否成功
                if (!this.loadDbConfig(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/db/res/config.xml")) {
                        throw new RuntimeException("Load Db Config Error");
                }
                // 判断是否编译Db源码
                if (1 == Framework.DB_SOURCE_CODE_REBUILD) {
                        // 编译Db源码
                        StringBuilder stderr = new StringBuilder();
                        try {
                                Run.executeProgram(Framework.DB_SOURCE_CODE_COMMAND, null, stderr, true);
                        } catch (Exception e) {
                                throw new RuntimeException("Run Db Source Code Command Error: " + System.getProperty("line.separator") + e.toString());
                        }
                        if (stderr.length() > 1) {
                                throw new RuntimeException("Complie Db Source Code Command Error: " + System.getProperty("line.separator") + stderr);
                        } else {
                                System.out.println("Db Source Complie Complete.");
                        }
                }
                try {
                        // 将编译好的Log文件，复制到classes目录
                        InputOutput.copyDirectory(InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/db/bin"), InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/classes"));
                } catch (Exception e) {
                        throw new RuntimeException("Copy Log Class Error: " + System.getProperty("line.separator") + e.toString());
                }
                // 根据DbFactory初始化Framework的Db对象
                if (!DbFactory.init(Framework.DB_INFO_DRIVER, Framework.DB_INFO_URL, Framework.DB_SECURITY_NAME, Framework.DB_SECURITY_PASSWORD, Framework.DB_POOL_MAXACTIVECONNECTION)) {
                        throw new RuntimeException("DbFactory Initialize Error");
                }
                Framework.LOG.info(LoadResource.MODULE_NAME, "The [Db Module] Initialization Is Complete");
                /************************************************
                 * 初始化模块
                 ************************************************/
                // 解压模块依赖jar至WEB-INF/classes目录
                jarList = InputOutput.getCurrentDirectoryAllFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module/", ".jar");
                if (null != jarList) {
                        Iterator<String> jarIter = jarList.iterator();
                        while (jarIter.hasNext()) {
                                String p = jarIter.next();
                                File f = new File(p);
                                if (f.isFile()) {
                                        try {
                                                InputOutput.decompressDirectoryToJarFile(f.getAbsolutePath(), Framework.PROJECT_REAL_PATH + "WEB-INF/classes/");
                                        } catch (Exception e) {
                                                throw new RuntimeException("Decompress Module Jar File Error: " + System.getProperty("line.separator") + e.toString());
                                        }
                                }
                        }
                }
                // 遍历所有模块，加载配置信息，加载sql信息，编译模块，复制模块classes文件。
                ArrayList<String> moduleList = InputOutput.getCurrentDirectoryFolderName(InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module/"));
                if (null != moduleList) {
                        Iterator<String> moduleIter = moduleList.iterator();
                        while (moduleIter.hasNext()) {
                                String moduleName = moduleIter.next();
                                // 加载配置信息
                                File file = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + moduleName + "/res/config.xml");
                                if (!file.exists()) {
                                        throw new RuntimeException("Module[" + moduleName + "] Config File Miss");
                                }
                                SAXReader reader = new SAXReader();
                                Document doc = null;
                                try {
                                        doc = reader.read(file);
                                } catch (Exception e) {
                                        throw new RuntimeException("Read Module[" + moduleName + "] Config File Error: " + System.getProperty("line.separator") + e.toString());
                                }
                                Element root = doc.getRootElement();
                                Element sourceCode = root.element("SourceCode");
                                Element module = root.element("Module");
                                if (module.attributeValue("enable").equalsIgnoreCase("false")) {
                                        Framework.LOG.info(LoadResource.MODULE_NAME, "Module[" + moduleName + "] Disable");
                                        continue;
                                }
                                Element docs = root.element("Docs");
                                int rebuild = Integer.parseInt(sourceCode.attributeValue("rebuild"));
                                String cmd = sourceCode.attributeValue("command").replaceAll("\\$\\{WEB_APP\\}/", Framework.PROJECT_REAL_PATH);
                                if (!SystemKit.isWindows()) {
                                        // windows系统下文件分隔用分号
                                        cmd = cmd.replaceAll(";", ":");
                                }
                                // 判断是否编译模块源码
                                if (1 == rebuild) {
                                        // 编译模块源码
                                        StringBuilder stderr = new StringBuilder();
                                        try {
                                                Run.executeProgram(cmd, null, stderr, true);
                                        } catch (Exception e) {
                                                throw new RuntimeException("Run Module[" + moduleName + "] Source Code Command Error: " + System.getProperty("line.separator") + e.toString());
                                        }
                                        if (stderr.length() > 1) {
                                                throw new RuntimeException("Complie Module[" + moduleName + "] Source Code Command Error: " + System.getProperty("line.separator") + stderr);
                                        } else {
                                                Framework.LOG.info(LoadResource.MODULE_NAME, "Module[" + moduleName + "] Source Complie Complete");
                                        }
                                }
                                try {
                                        // 将编译好的模块文件，复制到classes目录
                                        InputOutput.copyDirectory(InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + moduleName + "/bin"), InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/classes"));
                                } catch (Exception e) {
                                        throw new RuntimeException("Copy Module[" + moduleName + "] Class Error: " + System.getProperty("line.separator") + e.toString());
                                }
                                // 加载sql信息
                                file = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + moduleName + "/res/sql.xml");
                                if (!file.exists()) {
                                        throw new RuntimeException("Module[" + moduleName + "] Sql File Miss");
                                }
                                reader = new SAXReader();
                                try {
                                        doc = reader.read(file);
                                } catch (Exception e) {
                                        throw new RuntimeException("Read Module[" + moduleName + "] Config File Error: " + System.getProperty("line.separator") + e.toString());
                                }
                                root = doc.getRootElement();
                                SqlRepository.put(moduleName, root);
                                // 动态加载模块为Servlet
                                Dynamic moduleConfig = sce.getServletContext().addServlet("InitModuleConfig_" + moduleName, "module." + moduleName + ".necessary.Config");
                                moduleConfig.setInitParameter("path", Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + moduleName + "/res/config.xml");
                                moduleConfig.setLoadOnStartup(1);
                                // 初始化守护资源
                                Object[] params = { sce.getServletContext() };
                                Class<?>[] paramsType = { ServletContext.class };
                                try {
                                        Class<?> daemonClass = Class.forName("module." + moduleName + ".necessary.Daemon");
                                        Method daemonMethod = daemonClass.getMethod("run");
                                        Constructor<?> c = daemonClass.getConstructor(paramsType);
                                        Object o = c.newInstance(params);
                                        daemonMethod.invoke(o);
                                } catch (Exception e) {
                                        throw new RuntimeException("Module[" + moduleName + "] Daemon Invoke Error: " + System.getProperty("line.separator") + e.toString());
                                }
                                // 统一注册Dispatch为Servlet
                                Dynamic dispatchServlet = sce.getServletContext().addServlet(moduleName, Dispatch.class.getName());
                                // 设置模块的配置文件为Servlet的读取参数
                                dispatchServlet.setInitParameter("moduleName", moduleName);
                                dispatchServlet.setInitParameter("path", Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + moduleName + "/res/dispatch.xml");
                                dispatchServlet.setLoadOnStartup(3);
                                // 添加模块的映射
                                dispatchServlet.addMapping("/module/" + moduleName + "/*");
                                // 如果开启了api，那么添加api的servlet
                                if (docs.attributeValue("enable").equalsIgnoreCase("true")) {
                                        String moduleDocs = "docs_" + moduleName;
                                        // 统一注册Module为Servlet
                                        ServletRegistration srDocs = sce.getServletContext().addServlet(moduleDocs, ModuleDocs.class.getName());
                                        // 设置模块的配置文件为Servlet的读取参数
                                        srDocs.setInitParameter("path", Framework.PROJECT_REAL_PATH + "WEB-INF/module/" + moduleName + "/res/dispatch.xml");
                                        // 添加模块的映射
                                        srDocs.addMapping("/module/docs/" + moduleName);
                                }
                                // 最后保存模块名称
                                Framework.MODULE_NAME_LIST.add(moduleName);
                        }
                }
                Framework.LOG.info(LoadResource.MODULE_NAME, "Resource Load Complete!");
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
                DaemonAction.releaseDaemonThreadResource();
                DbFactory.releaseResource();
                Framework.LOG.releaseLogResource();
        }

}