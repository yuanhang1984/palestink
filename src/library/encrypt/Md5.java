package library.encrypt;

import java.security.MessageDigest;

public class Md5 {
        /**
         * 字节转十六进制方法（实现md5加密的内部调用）
         * 
         * @param b 待转换数据byte[]
         * @return 转换后的十六进制字符串
         */
        private static String byte2hex(byte b[]) {
                StringBuffer bf = new StringBuffer(b.length * 2);
                for (int i = 0; i < b.length; i++) {
                        if (((int) b[i] & 0xff) < 0x10)
                                bf.append("0");
                        bf.append(Long.toString((int) b[i] & 0xff, 16));
                }
                return bf.toString();
        }

        /**
         * md5加密方法
         * 
         * @param b 明文byte[]
         * @return 密文字符串
         */
        public static String encode(byte[] b) throws Exception {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(b);
                return byte2hex(digest).toLowerCase();
        }
}