package library.execute;

import java.io.InputStream;

/**
 * 运行程序
 */
public class Run {
        /**
         * 运行程序方法
         * 
         * @param cmd 运行程序的命令
         * @param stdout 标准输出流的返回对象
         * @param stderr 标准错误流的返回对象
         * @param isBlock true，阻塞运行程序；false，非阻塞运行。
         */
        public static void executeProgram(String cmd, StringBuilder stdout, StringBuilder stderr, boolean isBlock) throws Exception {
                Process p = Runtime.getRuntime().exec(cmd);
                InputStream stdoutIs = p.getInputStream();
                InputStream stderrIs = p.getErrorStream();
                int res = -1;
                byte[] b = new byte[512];
                boolean isStdoutFinish = true;
                boolean isStderrFinish = true;
                try {
                        if ((null != stdoutIs) || (null != stderrIs)) {
                                for (;;) {
                                        if (null != stdoutIs) {
                                                res = stdoutIs.read(b, 0, b.length);
                                                if (-1 != res) {
                                                        isStdoutFinish = false;
                                                        stdout.append(new String(b, 0, res));
                                                } else {
                                                        isStdoutFinish = true;
                                                }
                                                if (res < b.length) {
                                                        isStdoutFinish = true;
                                                }
                                        }
                                        if (null != stderrIs) {
                                                res = stderrIs.read(b, 0, b.length);
                                                if (-1 != res) {
                                                        isStderrFinish = false;
                                                        stderr.append(new String(b, 0, res));
                                                } else {
                                                        isStderrFinish = true;
                                                }
                                                if (res < b.length) {
                                                        isStderrFinish = true;
                                                }
                                        }
                                        if (isStdoutFinish && isStderrFinish) {
                                                break;
                                        }
                                }
                        }
                        if (isBlock) {
                                p.waitFor();
                        }
                } finally {
                        if (null != stdoutIs) {
                                stdoutIs.close();
                        }
                        if (null != stderrIs) {
                                stderrIs.close();
                        }
                }
        }
}