package library.encrypt;

/**
 * base64加密和解密
 */
public class Base64 {
        /**
         * base64加密
         * 
         * @param b 明文byte[]
         * @return 密文字符串
         */
        public static String encode(byte[] b) {
                return (java.util.Base64.getEncoder().encodeToString(b));
                // return (new BASE64Encoder().encode(b));
        }

        /**
         * base64解密
         * 
         * @param s 密文字符串
         * @return 明文byte[]
         */
        public static byte[] decode(String s) throws Exception {
                return (java.util.Base64.getDecoder().decode(s));
                // return new BASE64Decoder().decodeBuffer(s);
        }
}