package library.system;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

public class SystemKit {
        /*
         * 系统名称（小写）
         */
        private static String OS_NAME = System.getProperty("os.name").toLowerCase();

        /**
         * 判断是否为windows系统
         * @return 如果是windows系统返回true，如果不是返回false。
         */
        public static boolean isWindows() {
                if (-1 != OS_NAME.indexOf("windows")) {
                        return true;
                }
                return false;
        }

        /**
         * 获取剪切板文本内容
         * @return 剪切板文本内容
         * @throws Exception
         */
        public static String getClipboardText() throws Exception {
                String s = "";
                Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable t = c.getContents(null);
                if (null != t) {
                        // 检查内容是否是文本类型
                        if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                                s = (String) t.getTransferData(DataFlavor.stringFlavor);
                        }
                }
                return s;
        }
}