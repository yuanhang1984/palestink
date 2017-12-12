package library.encrypt;

import java.security.Signature;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.Cipher;

/**
 * rsa加密和解密
 */
public class Rsa {
        private static final String RSA_ALGORITHM = "RSA";
        private static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
        private static final String RSA_SIGN_ALGORITHM = "SHA1WithRSA";

        /**
         * 还原公共密钥方法
         * 
         * @param b 密文byte[]
         * @return 公共密钥
         */
        public static PublicKey restorePublicKey(byte[] b) throws Exception {
                KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
                PublicKey pk = kf.generatePublic(new X509EncodedKeySpec(b));
                return pk;
        }

        /**
         * 还原私有密钥方法
         * 
         * @param b 密文byte[]
         * @return 私有密钥
         */
        public static PrivateKey restorePrivateKey(byte[] b) throws Exception {
                KeyFactory kf = KeyFactory.getInstance(RSA_ALGORITHM);
                PrivateKey pk = kf.generatePrivate(new PKCS8EncodedKeySpec(b));
                return pk;
        }

        /**
         * rsa签名方法
         * 
         * @param c 待签名的明文
         * @param privateKey 私有密钥字符串
         * @return 签名字符串
         */
        public static String sign(byte[] c, String privateKey) throws Exception {
                PrivateKey pk = restorePrivateKey(Base64.decode(privateKey));
                Signature s = Signature.getInstance(RSA_SIGN_ALGORITHM);
                s.initSign(pk);
                s.update(c);
                byte[] b = s.sign();
                return Base64.encode(b);
        }

        /**
         * rsa加密方法
         * 
         * @param key 公共密钥
         * @param b 明文byte[]
         * @return 密文byte[]
         */
        public static byte[] encode(PublicKey key, byte[] b) throws Exception {
                Cipher c = Cipher.getInstance(RSA_TRANSFORMATION);
                c.init(Cipher.ENCRYPT_MODE, key);
                return c.doFinal(b);
        }

        /**
         * rsa解密方法
         * 
         * @param key 私有密钥
         * @param b 密文byte[]
         * @return 明文byte[]
         */
        public static byte[] decode(PrivateKey key, byte[] b) throws Exception {
                Cipher c = Cipher.getInstance(RSA_TRANSFORMATION);
                c.init(Cipher.DECRYPT_MODE, key);
                return c.doFinal(b);
        }
}