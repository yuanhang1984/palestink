package library.encrypt;

import javax.crypto.spec.SecretKeySpec;

public class Mac {
        public static enum ALGORITHM {
                HMACSHA1, HMACSHA256
        }

        /**
         * mac加密方法
         * 
         * @param key 加密key
         * @param data 明文byte[]
         * @param algorithm 加密选用的算法
         * @return 密文byte[]
         */
        public static byte[] encodeHmac(byte[] key, byte[] data, ALGORITHM algorithm) throws Exception {
                String macAlgorithm = "";
                switch (algorithm) {
                        case HMACSHA1:
                                macAlgorithm = "HmacSHA1";
                                break;
                        case HMACSHA256:
                                macAlgorithm = "HmacSHA256";
                                break;
                }
                javax.crypto.Mac m = javax.crypto.Mac.getInstance(macAlgorithm);
                m.init(new SecretKeySpec(key, 0, key.length, macAlgorithm));
                return m.doFinal(data);
        }
}