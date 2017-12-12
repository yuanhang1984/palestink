package framework.log;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.BufferedOutputStream;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import library.io.InputOutput;
import library.string.CharacterString;
import framework.sdk.Log;
import framework.sdk.Framework;

class CompressLogThread extends Thread {
        private String logFileName;
        private String zipFilePath;
        private LinkedList<String> logMessageList;

        public CompressLogThread(LinkedList<String> logMessageList, String logFileName, String zipFilePath) {
                this.logMessageList = logMessageList;
                this.logFileName = logFileName;
                this.zipFilePath = zipFilePath;
        }

        @Override
        public void run() {
                StringBuilder sb = null;
                ByteArrayInputStream bais = null;
                try {
                        sb = new StringBuilder();
                        for (int i = 0; i < this.logMessageList.size(); i++) {
                                sb.append(this.logMessageList.get(i));
                        }
                        // 压缩zip
                        bais = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));
                        InputOutput.compressDataToZipFile(bais, this.logFileName, this.zipFilePath);
                } catch (Exception e) {
                        System.err.println(e.toString());
                } finally {
                        try {
                                if (null != bais) {
                                        bais.close();
                                }
                        } catch (Exception e) {
                                System.err.println(e.toString());
                        }
                }
        }
}

public class LogFactory extends Log {
        /*
         * 日志输出线程的最大数量
         */
        private final int threadPoolMaxNum = 10;

        /*
         * 日志输出线程池（固定）
         */
        private ExecutorService logThreadPool;

        /*
         * 当前日志文件尺寸
         */
        private long currentLogFileSize;

        /*
         * 日志文件名
         */
        private String logFileName;

        /*
         * 日志输出路径
         */
        private String logFileOutputPath;

        /*
         * 压缩日志输出路径
         */
        private String logZipOutputPath;

        /*
         * 日志文件压缩成zip的尺寸（单位：MB）
         */
        private int logZipSize;

        /*
         * 日志File
         */
        private File logFile;

        /*
         * 日志消息列表
         */
        private LinkedList<String> logMessageList;

        /*
         * 文件读取对象
         */
        private FileReader fr = null;

        /*
         * 文件读取缓存对象
         */
        private BufferedReader br = null;

        /*
         * 文件输出流
         */
        private FileOutputStream fos = null;

        /*
         * 文件缓存输出流
         */
        private BufferedOutputStream bos = null;

        /*
         * 反射调用的类
         */
        private Class<?> logListenerClass = null;

        /*
         * 反射调用的构造函数
         */
        private Constructor<?> logListenerConstructor = null;

        /*
         * 反射调用的对象
         */
        private Object logListenerObject = null;

        /*
         * before反射调用方法
         */
        private Method beforeMethod = null;

        /*
         * after反射调用方法
         */
        private Method afterMethod = null;

        /**
         * 构造函数
         * @param logFileName 日志文件名
         * @param logZipOutputPath 日志文件输出路径
         * @param logZipOutputPath 压缩日志输出路径
         * @param logZipSize 日志压缩时的尺寸
         */
        public LogFactory(String logFileName, String logFileOutputPath, String logZipOutputPath, int logZipSize) {
                this.logFileName = logFileName;
                this.logFileOutputPath = logFileOutputPath;
                this.logZipOutputPath = logZipOutputPath;
                this.logZipSize = logZipSize;
                try {
                        this.logListenerClass = Class.forName("ext.log.necessary.LogListener");
                        this.beforeMethod = this.logListenerClass.getMethod("before", String.class, String.class);
                        this.afterMethod = this.logListenerClass.getMethod("after", String.class, String.class);
                        this.logListenerConstructor = this.logListenerClass.getConstructor();
                        this.logListenerObject = this.logListenerConstructor.newInstance();
                } catch (Exception e) {
                        throw new RuntimeException("Reflect Invoke LogListener Error: " + System.getProperty("line.separator") + e.toString());
                }
        }

        /**
         * 初始化函数
         */
        @Override
        public boolean init() {
                // 后面都是关于文件记录的相关操作，如果不开启文件记录，直接返回true。
                if (!Framework.LOG_FILE_ENABLE) {
                        return true;
                }
                try {
                        if (null == this.logFileOutputPath) {
                                return false;
                        }
                        if (null == this.logZipOutputPath) {
                                return false;
                        }
                        // 初始化线程池
                        this.logThreadPool = Executors.newFixedThreadPool(threadPoolMaxNum);
                        // 初始化消息列表
                        this.logMessageList = new LinkedList<String>();
                        // 初始化日志File
                        this.logFile = new File(InputOutput.regulatePath(this.logFileOutputPath) + this.logFileName);
                        if (!this.logFile.exists()) {
                                this.currentLogFileSize = 0L;
                                this.logFile.createNewFile();
                        } else {
                                if (!this.logFile.isFile()) {
                                        return false;
                                }
                                this.currentLogFileSize = this.logFile.length();
                        }
                        this.fr = new FileReader(this.logFile);
                        this.br = new BufferedReader(this.fr);
                        String oldRecord = "";
                        while (null != (oldRecord = this.br.readLine())) {
                                oldRecord += System.getProperty("line.separator");
                                this.logMessageList.add(oldRecord);
                        }
                        this.fos = new FileOutputStream(this.logFile);
                        this.bos = new BufferedOutputStream(fos);
                        for (int i = 0; i < this.logMessageList.size(); i++) {
                                this.bos.write(this.logMessageList.get(i).getBytes("utf-8"));
                        }
                        return true;
                } catch (Exception e) {
                        System.err.println(e.toString());
                        return false;
                } finally {
                        try {
                                if (null != this.br) {
                                        this.br.close();
                                }
                                if (null != this.fr) {
                                        this.fr.close();
                                }
                        } catch (Exception e) {
                                System.err.println(e.toString());
                                return false;
                        }
                }
        }

        @Override
        public void releaseLogResource() {
                try {
                        if (null != this.bos) {
                                this.bos.close();
                        }
                        if (null != this.fos) {
                                this.fos.close();
                        }
                } catch (Exception e) {
                        System.err.println(e.toString());
                }
        }

        /**
         * 获取日志消息<br />
         * 这里自定义了一个“调用层级”<br />
         * 调用的第一层是“getLogMsg”<br />
         * 调用的第二层是“调用getLogMsg的方法”<br />
         * 调用的第三层是“调用Log4j对象输出信息的地方”<br />
         * 所以这里的index设置为2（数组从0开始计算）
         * 
         * @param msg 消息内容
         * @return
         */
        private String getLogMsg(String moduleName, String level, String msg) {
                StackTraceElement ste[] = (new Throwable()).getStackTrace();
                int index = 0;
                if (3 < ste.length) {
                        index = 3;
                }
                StackTraceElement s = ste[index];
                String r = Framework.LOG_FILE_FORMAT;
                r = r.replaceAll("\\$\\{MODULE_NAME\\}", moduleName);
                r = r.replaceAll("\\$\\{LEVEL\\}", level);
                r = r.replaceAll("\\$\\{DATETIME\\}", CharacterString.getCurrentFormatDateTime("yyyy-MM-dd HH:mm:ss:SSS"));
                r = r.replaceAll("\\$\\{CLASS_NAME\\}", s.getClassName());
                r = r.replaceAll("\\$\\{METHOD_NAME\\}", s.getMethodName());
                r = r.replaceAll("\\$\\{FILE_NAME\\}", s.getFileName());
                r = r.replaceAll("\\$\\{LINE_NUMBER\\}", String.valueOf(s.getLineNumber()));
                r = r.replaceAll("\\$\\{MESSAGE\\}", msg);
                return r;
        }

        @SuppressWarnings("unchecked")
        private void appendToLog(String moduleName, String level, String msg) {
                try {
                        String content = this.getLogMsg(moduleName, level, msg) + System.getProperty("line.separator");
                        // 控制台输出消息
                        System.out.print(content);
                        if (Framework.LOG_FILE_ENABLE) {
                                this.logMessageList.add(content);
                                this.currentLogFileSize += content.getBytes().length;
                                // 判断是否超过log文件尺寸限制
                                if ((this.logZipSize * 1024 * 1024) <= this.currentLogFileSize) {
                                        String currentTimestamp = CharacterString.getCurrentFormatDateTime("yyyyMMddHHmmssSSS");
                                        String zipFilePath = this.logZipOutputPath + this.logFileName.split("\\.")[0] + "_" + currentTimestamp + "." + "zip";
                                        CompressLogThread clt = new CompressLogThread((LinkedList<String>) this.logMessageList.clone(), this.logFileName, zipFilePath);
                                        this.logMessageList.clear();
                                        this.logThreadPool.execute(clt);
                                        // 关闭原有文件流
                                        if (null != this.bos) {
                                                this.bos.close();
                                        }
                                        if (null != this.fos) {
                                                this.fos.close();
                                        }
                                        // 日志文件大小置0
                                        this.currentLogFileSize = 0;
                                        // 重新生成文件流对象
                                        this.fos = new FileOutputStream(this.logFile);
                                        this.bos = new BufferedOutputStream(fos);
                                }
                                this.bos.write(content.getBytes("utf-8"));
                                this.bos.flush();
                        }
                } catch (Exception e) {
                        System.err.println(e.toString());
                }
        }

        @Override
        public void debug(String moduleName, String msg) {
                try {
                        String newMsg = (String) this.beforeMethod.invoke(this.logListenerObject, new Object[] { moduleName, msg });
                        this.appendToLog(moduleName, "debug", newMsg);
                        this.afterMethod.invoke(this.logListenerObject, new Object[] { moduleName, newMsg });
                } catch (Exception e) {
                        System.err.println(e.toString());
                }
        }

        @Override
        public void info(String moduleName, String msg) {
                try {
                        String newMsg = (String) this.beforeMethod.invoke(this.logListenerObject, new Object[] { moduleName, msg });
                        this.appendToLog(moduleName, "info", newMsg);
                        this.afterMethod.invoke(this.logListenerObject, new Object[] { moduleName, newMsg });
                } catch (Exception e) {
                        System.err.println(e.toString());
                }
        }

        @Override
        public void warn(String moduleName, String msg) {
                try {
                        String newMsg = (String) this.beforeMethod.invoke(this.logListenerObject, new Object[] { moduleName, msg });
                        this.appendToLog(moduleName, "warn", newMsg);
                        this.afterMethod.invoke(this.logListenerObject, new Object[] { moduleName, newMsg });
                } catch (Exception e) {
                        System.err.println(e.toString());
                }
        }

        @Override
        public void error(String moduleName, String msg) {
                try {
                        String newMsg = (String) this.beforeMethod.invoke(this.logListenerObject, new Object[] { moduleName, msg });
                        this.appendToLog(moduleName, "error", newMsg);
                        this.afterMethod.invoke(this.logListenerObject, new Object[] { moduleName, newMsg });
                } catch (Exception e) {
                        System.err.println(e.toString());
                }
        }

        @Override
        public void fatal(String moduleName, String msg) {
                try {
                        String newMsg = (String) this.beforeMethod.invoke(this.logListenerObject, new Object[] { moduleName, msg });
                        this.appendToLog(moduleName, "fatal", newMsg);
                        this.afterMethod.invoke(this.logListenerObject, new Object[] { moduleName, newMsg });
                } catch (Exception e) {
                        System.err.println(e.toString());
                }
        }
}