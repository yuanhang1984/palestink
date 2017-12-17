package framework.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import library.execute.Run;
import library.io.InputOutput;
import library.system.SystemKit;
import framework.sdk.Framework;
import framework.db.sdbo.DbFactory;
import framework.log.LogFactory;
import framework.sdk.DaemonAction;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

public class LoadResource implements ServletContextListener {
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

        // /**
        // * 清空临时文件夹下所有数据
        // */
        // private void clearTempDir() {
        // File f = new File(Framework.PROJECT_REAL_PATH + "WEB-INF/temp");
        // if (!f.exists()) {
        // f.mkdirs();
        // } else {
        // InputOutput.clearDir(f);
        // }
        // }
        // /**
        // * 加载模块的名称<br />
        // * 主要实现3个目的：<br />
        // * 1、保存模块名称。<br />
        // * 2、解压模块jar文件。<br />
        // * 3、将lib库依赖的jar文件，解压至WEB-INF/classes文件夹。<br />
        // * 4、将模块依赖的jar文件，解压至WEB-INF/classes文件夹。<br />
        // */
        // private void loadModule() {
        // // 保存模块名称
        // ArrayList<String> nameList = InputOutput.getCurrentDirectoryFileName(Framework.PROJECT_REAL_PATH + "WEB-INF/module", ".jar");
        // Iterator<String> nameIter = nameList.iterator();
        // while (nameIter.hasNext()) {
        // String name = nameIter.next();
        // name = name.substring(0, name.indexOf("."));
        // Framework.MODULE_NAME_LIST.add(name);
        // }
        // // 解压模块jar文件，并将模块中的class文件复制到WEB-INF/classes中去。
        // ArrayList<String> pathList = InputOutput.getCurrentDirectoryFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/module", ".jar");
        // Iterator<String> pathIter = pathList.iterator();
        // while (pathIter.hasNext()) {
        // String jarFilePath = pathIter.next();
        // File f = new File(jarFilePath);
        // String modName = f.getName();
        // modName = modName.substring(0, modName.indexOf("."));
        // try {
        // InputOutput.decompressDirectoryToJarFile(jarFilePath, Framework.PROJECT_REAL_PATH + "WEB-INF/temp/module/" + modName + "/");
        // InputOutput.copyDirectory(Framework.PROJECT_REAL_PATH + "WEB-INF/temp/module/" + modName + "/" + modName, Framework.PROJECT_REAL_PATH + "WEB-INF/classes/" + modName);
        // } catch (Exception e) {
        // Framework.LOG.warn(e.toString());
        // }
        // }
        // // 将lib库依赖的jar文件，解压至WEB-INF/classes文件夹
        // ArrayList<String> libList = InputOutput.getCurrentDirectoryFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/lib", ".jar");
        // Iterator<String> libIter = libList.iterator();
        // while (libIter.hasNext()) {
        // String libFilePath = libIter.next();
        // File f = new File(libFilePath);
        // String libName = f.getName();
        // libName = libName.substring(0, libName.indexOf("."));
        // try {
        // InputOutput.decompressDirectoryToJarFile(libFilePath, Framework.PROJECT_REAL_PATH + "WEB-INF/temp/lib/" + libName + "/");
        // } catch (Exception e) {
        // Framework.LOG.warn(e.toString());
        // }
        // }
        // // 将模块依赖的jar文件，解压至WEB-INF/classes文件夹
        // ArrayList<String> jarList = InputOutput.getCurrentDirectoryAllFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/temp/", ".jar");
        // Iterator<String> jarIter = jarList.iterator();
        // while (jarIter.hasNext()) {
        // String p = jarIter.next();
        // File f = new File(p);
        // if (f.isFile()) {
        // try {
        // InputOutput.decompressDirectoryToJarFile(f.getAbsolutePath(), Framework.PROJECT_REAL_PATH + "WEB-INF/classes/");
        // } catch (Exception e) {
        // Framework.LOG.warn(e.toString());
        // }
        // }
        // }
        // }

        // /**
        // * 注册模块
        // *
        // * @param sc Servlet环境
        // */
        // private void registerModule(ServletContext sc) {
        // // 设置初始化servlet
        // Iterator<String> iter = Framework.MODULE_NAME_LIST.iterator();
        // Framework.LOG.info("Lego Module Include: ");
        // while (iter.hasNext()) {
        // String modName = iter.next();
        // Framework.LOG.info("[" + modName + "]");
        // // 初始化模块配置资源
        // Dynamic initModuleConfig = sc.addServlet("InitModuleConfig_" + modName, modName + ".Config");
        // initModuleConfig.setInitParameter("path", Framework.PROJECT_REAL_PATH + "WEB-INF/temp/module/" + modName + "/resource/config/config.xml");
        // initModuleConfig.setLoadOnStartup(1);
        // // 初始化守护资源
        // Object[] params = { sc, new SimpleDBO() };
        // Class<?>[] paramsType = { ServletContext.class, DbHandler.class };
        // try {
        // Class<?> daemonClass = Class.forName(modName + ".Daemon");
        // Method daemonMethod = daemonClass.getMethod("run");
        // Constructor<?> c = daemonClass.getConstructor(paramsType);
        // Object o = c.newInstance(params);
        // daemonMethod.invoke(o);
        // } catch (Exception e) {
        // Framework.LOG.fatal(e.toString());
        // }
        // // 统一注册Dispatch为Servlet
        // Dynamic dispatchServlet = sc.addServlet(modName, Dispatch.class.getName());
        // // 设置模块的配置文件为Servlet的读取参数
        // dispatchServlet.setInitParameter("moduleName", modName);
        // dispatchServlet.setInitParameter("path", Framework.PROJECT_REAL_PATH + "WEB-INF/temp/module/" + modName + "/resource/config/dispatch.xml");
        // dispatchServlet.setLoadOnStartup(3);
        // // 添加模块的映射
        // dispatchServlet.addMapping("/lego/" + modName);
        // // 如果开启了api，那么添加api的servlet
        // if (Framework.DEBUG_ENABLE) {
        // String modNameApi = modName + "_api";
        // // 统一注册Module为Servlet
        // ServletRegistration srApi = sc.addServlet(modNameApi, ModuleApi.class.getName());
        // // 设置模块的配置文件为Servlet的读取参数
        // srApi.setInitParameter("path", Framework.PROJECT_REAL_PATH + "WEB-INF/temp/module/" + modName + "/resource/config/dispatch.xml");
        // // 添加模块的映射
        // srApi.addMapping("/lego/api/" + modName);
        // }
        // }
        // }

        // /**
        // * 加载数据库的配置
        // */
        // private void loadDbConfig(ServletContext sc) {
        // try {
        // String driver = sc.getInitParameter("db_driver");
        // String url = sc.getInitParameter("db_url");
        // String username = sc.getInitParameter("db_username");
        // String password = sc.getInitParameter("db_password");
        // String poolMaximumActiveConnections = sc.getInitParameter("db_poolMaximumActiveConnections");
        // this.generateMybatisConfigFile(driver, url, username, password, poolMaximumActiveConnections);
        // } catch (Exception e) {
        // Framework.LOG.fatal(e.toString());
        // }
        // }

        // /**
        // * 加载API的配置
        // *
        // * @param sc servlet环境
        // */
        // private void loadApiConfig(ServletContext sc) {
        // try {
        // String enable = sc.getInitParameter("debugEnable");
        // if (enable.equalsIgnoreCase("true")) {
        // Framework.DEBUG_ENABLE = true;
        // } else {
        // Framework.DEBUG_ENABLE = false;
        // }
        // } catch (Exception e) {
        // Framework.LOG.fatal(e.toString());
        // }
        // }

        // /**
        // * 生成mybatis配置文件
        // *
        // * @param driver 数据库驱动
        // * @param url 数据库url
        // * @param username 用户名
        // * @param password 密码
        // * @param poolMaximumActiveConnections 线程池最大连接数
        // */
        // private void generateMybatisConfigFile(String driver, String url, String username, String password, String poolMaximumActiveConnections) {
        // XMLWriter xw = null;
        // try {
        // // mybatis配置文件的路径
        // String path = Framework.PROJECT_REAL_PATH + "WEB-INF/temp/db/mybatis.xml";
        // File configFile = new File(path);
        // // 如果存在就先删除
        // if (configFile.exists()) {
        // // 未必一定删除
        // configFile.delete();
        // } else {
        // configFile.getParentFile().mkdirs();
        // }
        // // 创建mybatis配置文件所需的基本信息
        // Document doc = DocumentHelper.createDocument();
        // doc.addDocType("configuration", "-//mybatis.org//DTD Config 3.0//EN", "http://mybatis.org/dtd/mybatis-3-config.dtd");
        // doc.addComment("This Config File Made By PALESTINK Framework");
        // doc.addComment("因为框架实现了自定义日之类，所以这里不配置mybatis的log了。");
        // Element configuration = doc.addElement("configuration");
        // Element environments = configuration.addElement("environments");
        // environments.addAttribute("default", "development");
        // Element environment = environments.addElement("environment");
        // environment.addAttribute("id", "development");
        // Element transactionManager = environment.addElement("transactionManager");
        // transactionManager.addAttribute("type", "JDBC");
        // Element dataSource = environment.addElement("dataSource");
        // dataSource.addAttribute("type", "POOLED");
        // Element property = dataSource.addElement("property");
        // property.addAttribute("name", "driver");
        // property.addAttribute("value", driver);
        // property = dataSource.addElement("property");
        // property.addAttribute("name", "url");
        // property.addAttribute("value", url);
        // property = dataSource.addElement("property");
        // property.addAttribute("name", "username");
        // property.addAttribute("value", username);
        // property = dataSource.addElement("property");
        // property.addAttribute("name", "password");
        // property.addAttribute("value", password);
        // property = dataSource.addElement("property");
        // property.addAttribute("name", "poolMaximumActiveConnections");
        // property.addAttribute("value", poolMaximumActiveConnections);
        // // 添加模块映射的信息
        // Element mappers = configuration.addElement("mappers");
        // // 遍历模块的名字
        // Iterator<String> iter = Framework.MODULE_NAME_LIST.iterator();
        // while (iter.hasNext()) {
        // String modName = iter.next();
        // Element mapper = mappers.addElement("mapper");
        // mapper.addAttribute("resource", "../temp/module/" + modName + "/resource/config/mybatis.xml");
        // }
        // OutputFormat format = OutputFormat.createPrettyPrint();
        // format.setEncoding("utf-8");
        // xw = new XMLWriter(new FileWriter(configFile), format);
        // xw.write(doc);
        // xw.flush();
        // } catch (Exception e) {
        // Framework.LOG.fatal(e.toString());
        // } finally {
        // try {
        // if (null != xw) {
        // xw.close();
        // }
        // } catch (Exception e) {
        // Framework.LOG.fatal(e.toString());
        // }
        // }
        // }

        @Override
        public void contextInitialized(ServletContextEvent sce) {
                // 初始化Framework的所需路径
                Framework.PROJECT_REAL_PATH = InputOutput.regulatePath(sce.getServletContext().getRealPath("/"));
                // 清空classes文件夹下所有文件
                this.clearWICDir();
                // 解压ext依赖jar至WEB-INF/classes目录
                ArrayList<String> jarList = InputOutput.getCurrentDirectoryAllFilePath(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/", ".jar");
                Iterator<String> jarIter = jarList.iterator();
                while (jarIter.hasNext()) {
                        String p = jarIter.next();
                        File f = new File(p);
                        if (f.isFile()) {
                                try {
                                        InputOutput.decompressDirectoryToJarFile(f.getAbsolutePath(), Framework.PROJECT_REAL_PATH + "WEB-INF/classes/");
                                } catch (Exception e) {
                                        throw new RuntimeException("Decompress The Jar File Error: " + System.getProperty("line.separator") + e.toString());
                                }
                        }
                }
                // 初始化Log（因为很多地方需要用到Framework的日志打印，所以要提前初始化）
                // 判断加载log配置是否成功
                if (!this.loadLogConfig(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/log/res/config.xml")) {
                        throw new RuntimeException("Load Log Config Error");
                }
                // 判断是否编译Log源码
                if (1 == Framework.LOG_SOURCE_CODE_REBUILD) {
                        // 编译Log源码
                        StringBuilder stdout = new StringBuilder();
                        StringBuilder stderr = new StringBuilder();
                        try {
                                Run.executeProgram(Framework.LOG_SOURCE_CODE_COMMAND, stdout, stderr, true);
                                if (stderr.length() > 1) {
                                        throw new RuntimeException("Run Source Code Command Error: " + System.getProperty("line.separator") + stderr);
                                } else {
                                        System.out.println("Log Source Complie Complete.");
                                }
                        } catch (Exception e) {
                                throw new RuntimeException("Run Source Code Command Error: " + System.getProperty("line.separator") + e.toString());
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
                Framework.LOG.info(Framework.FRAMEWORK_MODULE_NAME, "The [Log Module] Initialization Is Complete");
                // 初始化Db
                // 判断加载db配置是否成功
                if (!this.loadDbConfig(Framework.PROJECT_REAL_PATH + "WEB-INF/ext/db/res/config.xml")) {
                        throw new RuntimeException("Load Db Config Error");
                }
                // 判断是否编译Db源码
                if (1 == Framework.DB_SOURCE_CODE_REBUILD) {
                        // 编译Db源码
                        StringBuilder stdout = new StringBuilder();
                        StringBuilder stderr = new StringBuilder();
                        try {
                                Run.executeProgram(Framework.DB_SOURCE_CODE_COMMAND, stdout, stderr, true);
                                if (stderr.length() > 1) {
                                        throw new RuntimeException("Run Source Code Command Error: " + System.getProperty("line.separator") + stderr);
                                } else {
                                        System.out.println("Db Source Complie Complete.");
                                }
                        } catch (Exception e) {
                                throw new RuntimeException("Run Source Code Command Error: " + System.getProperty("line.separator") + e.toString());
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
                Framework.LOG.info(Framework.FRAMEWORK_MODULE_NAME, "The [Db Module] Initialization Is Complete");
                //////////////////////////////////////////////////////
                //////////////////////////////////////////////////////
                //////////////////////////////////////////////////////
                //////////////////////////////////////////////////////
                //////////////////////////////////////////////////////
                //////////////////////////////////////////////////////
                //////////////////////////////////////////////////////
                //////////////////////////////////////////////////////
                // Framework.init(Framework.PROJECT_REAL_PATH + "WEB-INF/logs");
                // // 初始化Framework的LOG对象
                // Framework.LOG = new LogFactory("record.log", InputOutput.regulatePath(Framework.PROJECT_REAL_PATH + "WEB-INF/logs"), 10);
                // if (1 != Framework.LOG.init()) {
                // Framework.LOG.fatal("LogFactory Initialize Error");
                // return;
                // }
                // // 清空临时目录
                // this.clearTempDir();
                // // 清空WEB-INF/classes文件夹下所有数据
                // this.clearWICDir();
                // // 加载模块
                // this.loadModule();
                // // 根据配置文件生成mybatis配置文件
                // this.loadDbConfig(sce.getServletContext());
                // // 初始化数据库（数据库读取的mybatis是系统生成的，所以要放到loadDbConfig之后）
                // DbFactory.init(Framework.LOG, Framework.PROJECT_REAL_PATH + "WEB-INF/temp/db/mybatis.xml");
                // // 根据配置文件检查是否开启模块api功能
                // this.loadApiConfig(sce.getServletContext());
                // // 注册模块（api模块需要检查是否开启api的选项，所以要放到loadApiConfig之后）
                // this.registerModule(sce.getServletContext());
                // // 加载用户角色
                // Dispatch.loadUserRole();
                // Framework.LOG.info("Resource Load Complete!");
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
                DaemonAction.releaseDaemonThreadResource();
                DbFactory.releaseResource();
                Framework.LOG.releaseLogResource();
        }

}