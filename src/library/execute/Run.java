package library.execute;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 运行程序
 */
public class Run {
        /**
         * 运行程序方法
         * 注意，如果没有输出流，只有错误流。一定要将输出流置null，否则将会一直等待输出流的数据。相反同理。
         * 
         * @param cmd 运行程序的命令
         * @param stdout 标准输出流的返回对象
         * @param stderr 标准错误流的返回对象
         * @param isBlock true，阻塞运行程序；false，非阻塞运行。
         */
        public static void executeProgram(String cmd, StringBuilder stdout, StringBuilder stderr, boolean isBlock) throws Exception {
                Process p = Runtime.getRuntime().exec(cmd);
                InputStream stdoutIs = null;
                InputStream stderrIs = null;
                InputStreamReader stdoutIsr = null;
                InputStreamReader stderrIsr = null;
                BufferedReader stdoutBr = null;
                BufferedReader stderrBr = null;
                if (null != stdout) {
                        stdoutIs = p.getInputStream();
                }
                if (null != stderr) {
                        stderrIs = p.getErrorStream();
                }
                if (null != stdoutIs) {
                        stdoutIsr = new InputStreamReader(stdoutIs);
                }
                if (null != stderrIs) {
                        stderrIsr = new InputStreamReader(stderrIs);
                }
                if (null != stdoutIsr) {
                        stdoutBr = new BufferedReader(stdoutIsr);
                }
                if (null != stderrIsr) {
                        stderrBr = new BufferedReader(stderrIsr);
                }
                try {
                        String res = null;
                        if (null != stdoutBr) {
                                while (null != (res = stdoutBr.readLine())) {
                                        stdout.append(res);
                                        stdout.append(System.getProperty("line.separator"));
                                }
                        }
                        if (null != stderrBr) {
                                while (null != (res = stderrBr.readLine())) {
                                        stderr.append(res);
                                        stderr.append(System.getProperty("line.separator"));
                                }
                        }
                        if (isBlock) {
                                p.waitFor();
                        }
                } finally {
                        if (null != stdoutBr) {
                                stdoutBr.close();
                        }
                        if (null != stderrBr) {
                                stderrBr.close();
                        }
                        if (null != stdoutIsr) {
                                stdoutIsr.close();
                        }
                        if (null != stderrIsr) {
                                stderrIsr.close();
                        }
                        if (null != stdoutIs) {
                                stdoutIs.close();
                        }
                        if (null != stderrIs) {
                                stderrIs.close();
                        }
                }
        }
}