package library.payment;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import library.encrypt.Rsa;
import library.string.CharacterString;
import org.json.JSONObject;

/**
 * 支付宝支付功能（尚未完成）
 */
public class Alipay {
        /*
         * 支付宝配置参数
         */
        public static final String PARTNER = "2088421589496659";
        public static final String APP_ID = "2016091000478100";
        public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJsdggaoFbfCZknpGt+VfBNtpXHSSZH6L1N1Ejimj2Zz1kewgE9HKjqPBgoIFROhGCZbTxbR9KJKOp91EKzIUGhI3HAroB3dQpRjOimW16E9W5t5M1NNRUNz23/wX6rxl2DRsQC6MLy7Gs+E0n/5jkxfebC4yTkX4dbVe4SHJccQIDAQAB";
        public static final String PUBLIC_KEY_ALIPAY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDIgHnOn7LLILlKETd6BFRJ0GqgS2Y3mn1wMQmyh9zEyWlz5p1zrahRahbXAfCfSqshSNfqOmAQzSHRVjCqjsAw1jyqrXaPdKBmr90DIpIxmIyKXv4GGAkPyJ/6FTFY99uhpiq0qadD/uSzQsefWo0aTvP/65zi3eof7TcZ32oWpwIDAQAB";
        public static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMmx2CBqgVt8JmSeka35V8E22lcdJJkfovU3USOKaPZnPWR7CAT0cqOo8GCggVE6EYJltPFtH0oko6n3UQrMhQaEjccCugHd1ClGM6KZbXoT1bm3kzU01FQ3Pbf/BfqvGXYNGxALowvLsaz4TSf/mOTF95sLjJORfh1tV7hIclxxAgMBAAECgYArhqysei+GTal/Z1Tq5XdRpAPEbu6Z+ODS6GgjMlhqbGcsSvy1DQe8V9XgFGMz40MqD8bXnwP0nSmQoWWk3fQKzPANkou2Ad6flimS1oa1BtR+yDJmj/ppNO4ZYUebsMXjXYSTMxDVh3iwG458gs4jpgNIez/p+00ZYPiVCDWKDQJBAOiovjLSx1YE/HGL2FpZdkrdrEOHQMWaF2SB/Mw3leX4wQBcOgN3qX+68ODZn+YussFoqBSt95ZeNgjrK/WNG4MCQQDd7du/kjeWcQGgvUaURNc/TX7Xq5vFD+V7AtykVbdM6M8yuNmMyyrUjHju4GgqBQWKlAIkKzUzWCYg4/ozGaH7AkEAulhlNnnCNLB7rUov+HWNHHud/Nw40cwQjmzamIqw4egyVWKJCLvwI6EMS1ujCY8/l0+GyEFqe6JrrwsFQ4BIlwJBANMHUMPzV6RYRMIB+VEXvM3W9NxDtFVIbl/wMrqZPzmEzzFeEJAOyOVfxcbv4FzEdaZ2YyrdYzM1iNwRrGJW520CQEWcAivmL4D/qZIGYaDVifIsmcdKoaf0VyK8e9hCfKl4YWzXqfO+W8kRQxQSuHaD0LYhCJLxFc8jW9TzszSv+Fk=";
        public static final String MD5_KEY = "jsxpbfnzaggwcjzvv27o73vpsbchngqd";
        /*
         * 常量参数
         */
        public static final String WAP_METHOD = "alipay.trade.wap.pay"; // 手机网站支付接口
        public static final String APP_METHOD = "alipay.trade.app.pay"; // App支付接口
        public static final String FORMAT = "JSON";
        public static final String RETURN_URL = "";
        // public static final String CHARSET = "utf-8";
        public static final String CHARSET = "gbk";
        public static final String SIGN_TYPE_RSA = "RSA";
        public static final String SIGN_TYPE_MD5 = "MD5";
        public static final String VERSION = "1.0";
        public static final String NOTIFY_URL = "";
        public static final String WAP_PRODUCT_CODE = "QUICK_WAP_PAY";

        // 沙箱环境
        // public static final String PARTNER = "2088421589496659";
        // public static final String APP_ID = "2016091000478100";
        // public static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANfB2n/HoyK6uqcKs0+hWJk00q0a5Z1zJofiw8ecZdG7o4f/vJYAFu0DftZ8i7Hij/Onxh3puP39N8Ky8b5cB1bGs3MQAGSO09l9mwHbcj5L5YTTSILo3J7ovR4gFfnyIKMXACcXs9Yg3CKls21cjcLpZZCC9kmJnLOyautKLv+vAgMBAAECgYA0Jp5K3pV3Eo2FTNfUupowxgzaXLL9oNTCabSK7inKTUGU4OLTmwfvmDWzYH1frPTAn2AK4PCfK/KrdBdsvM/k3vuSxv1CGrL6j3JwMLRw0Q5+NChL3XVGiZccSRsdMXh2Aq/wiJ4oSsjs1IxKuv46uGVOlcQTZzij2FN2XJUr0QJBAPY4mH2/i1Sw+SVBR/vB89oO39AOpmgHnW3etDSfCdpBixVIgKusrDx401+oaJQLM+6HowDOC2lWYQdWMYiSkGcCQQDgU4XVC2NFhzJ3ZSAJ7Be9qLIf3uSaipH1Nc/wKS81A7z84twlnwrZO82gKzCfUTmPpBK5oDdZmHt5bj6Dzel5AkBFFF3cMehSa5CLHkSm4qSa0j+C7QlM+I33rFrcxJ4MCQWPhBbtk3WDRdbv6JzmpDn+uzlFWYmkFSMF3u3oxk4TAkEAn2r72xFCtTCFLtBQ2nxLyt7N51RRhXogi/B4G6ZJBBXqApV9+cZywTp8wOywmyfwDiJ9pCk17Jgud3dfOe/lkQJAJiefSfrJyDZnPfL/aXNFqSMYgQM7SyqOUCiAZfy53aYSN18xNck1IJ30WTDYEo7qYFfYBkiJLiudr3iwruuVfQ==";
        // jack
        // public static final String PARTNER = "2088002179431119";
        // public static final String APP_ID = "2016093002016861";
        // public static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMQlHVT70ara2yapEkoTLobEEtJdY1qtBqpmz+//eNdPVwaCY19rqaXZPd3LoR69J087cOaOoGl7nagWyeVsNtUAGUlYVZUVTN+G5o4wBfWkFlz6v0eOuojblMI/PqFEJ0eP+Gvc1idcopeiz+qRygM10K1uvZCOWIJeY9SuHwu5AgMBAAECgYBo6CDkDzt40k1cRBlQHSpJUh1u/hvhp6FlClA04MLVBc5Z4OsdYgv+dE6ujfbC5hBcREKCTc5mHIOaH3YkithX7FkCiCAvl2o3cHagqwaJrViB65IaU0vcJes3plPDXdNDOpOrmfrlFy55aZ3cWsVXX/JHbh/7T+PUpdcDoaG+8QJBAOLva0O+jSFq+9TO74KSyu5xEpEM2jEOaV3sSB+Y/P9QCRpGSQPWzmVruA5AwuUeHR46aUSzikq8nXYum+VPa/8CQQDdRCsFaH7ifKwVFSDfAI5jPzkloPkGzc99mrS3EO5uVZXxA9w52htWt50tQ8Tl8n4j5zFgd/l4K7WghRTYx+hHAkEAlMraD67upgyZVazgyUIqIDCvNb+xiByk5Vo4587NgzEc5sdStFuxNDpukZnQr50RBnLR9qOpdts4evaQbMeoTQJBALAIKp3b245+lz0SmRsxSxaEVwerwFL8bAXGjd1S2A3pgyfC5XMVJUjhPWekkcJLILWZmfvglxVW2OHC3qn0qMMCQEtPvZOAxj8EtjjnB+WqiLWm3u30GVFh+779w9KcLIG5Xom+Ua+ZjZCaAFl3QvHU1xRC9ilcr1G6hvWWfAVmnlQ=";
        // public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
        // public static final String MD5_KEY = "8mx8nnz7xdms53dzluka05og9zd9hf43";

        // public static final String METHOD = "alipay.trade.app.pay"; // APP
        // public static final String METHOD = "alipay.trade.wap.pay"; // 手机网站
        // public static final String PRODUCT_CODE = "QUICK_MSECURITY_PAY";

        private static ArrayList<Entry<String, String>> sortParameter(HashMap<String, String> m) {
                ArrayList<Entry<String, String>> list = new ArrayList<Entry<String, String>>(m.entrySet());
                Collections.sort(list, new Comparator<Entry<String, String>>() {
                        public int compare(Entry<String, String> m1, Entry<String, String> m2) {
                                return m1.getKey().compareTo(m2.getKey());
                        }
                });
                return list;
        }

        private static String createParameterLinkString(ArrayList<Entry<String, String>> list) {
                String r = "";
                Iterator<Entry<String, String>> iter = list.iterator();
                while (iter.hasNext()) {
                        Entry<String, String> e = (Entry<String, String>) iter.next();
                        r += e.getKey() + "=" + e.getValue() + "&";
                }
                return r.substring(0, r.length() - 1);
        }

        // /**
        // * @param body(128):对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
        // * @param subject(256):商品的标题/交易标题/订单标题/订单关键字等。
        // * @param out_trade_no(64):商户网站唯一订单号
        // * @param timeout_express(6):该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
        // * 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
        // * @param total_amount(9):订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
        // * @param seller_id(16):收款支付宝用户ID。
        // * 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
        // * @param product_code(64):销售产品码，商家和支付宝签约的产品码
        // * @return
        // */
        // public static String createAlipayBizContentForApp(String body, String subject, String out_trade_no, String timeout_express, String total_amount, String seller_id, String product_code) {
        // if (body.length() > 128)
        // return null;
        // if (subject.length() > 256)
        // return null;
        // if (out_trade_no.length() > 64)
        // return null;
        // if (timeout_express.length() > 6)
        // return null;
        // if (total_amount.length() > 9)
        // return null;
        // if (seller_id.length() > 16)
        // return null;
        // if (product_code.length() > 64)
        // return null;
        // JSONObject o = new JSONObject();
        // o.put("body", body);
        // o.put("subject", subject);
        // o.put("out_trade_no", out_trade_no);
        // o.put("timeout_express", timeout_express);
        // o.put("total_amount", total_amount);
        // o.put("seller_id", seller_id);
        // o.put("product_code", product_code);
        // return o.toString();
        // }

        /**
         * @param body 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
         * @param body (128):对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
         * @param subject (256):商品的标题/交易标题/订单标题/订单关键字等。
         * @param out_trade_no (64):商户网站唯一订单号
         * @param timeout_express(6):该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点， 如 1.5h，可转换为 90m。
         * @param total_amount(9):订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
         * @param seller_id(28):收款支付宝用户ID。 如果该值为空，则默认为商户签约账号对应的支付宝用户ID
         * @param auth_token(40):针对用户授权接口，获取用户相关数据时，用于标识用户授权关系。
         * @param product_code(64):销售产品码，商家和支付宝签约的产品码
         * @return
         */
        public static String createAlipayBizContentForWap(String body, String subject, String out_trade_no, String timeout_express, String total_amount, String seller_id, String auth_token, String product_code) {
                if (body.length() > 128)
                        return null;
                if (subject.length() > 256)
                        return null;
                if (out_trade_no.length() > 64)
                        return null;
                if (timeout_express.length() > 6)
                        return null;
                if (total_amount.length() > 9)
                        return null;
                if (seller_id.length() > 28)
                        return null;
                if (auth_token.length() > 40)
                        return null;
                if (product_code.length() > 64)
                        return null;
                JSONObject o = new JSONObject();
                o.put("body", body);
                o.put("subject", subject);
                o.put("out_trade_no", out_trade_no);
                o.put("timeout_express", timeout_express);
                o.put("total_amount", total_amount);
                o.put("seller_id", seller_id);
                o.put("auth_token", auth_token);
                o.put("product_code", product_code);
                return o.toString();
        }

        // public static String createAlipayStringForApp(String app_id, String method, String format, String charset, String sign_type, String version, String notify_url, String biz_content) {
        // try {
        // HashMap<String, String> m = new HashMap<String, String>();
        // m.put("app_id", app_id);
        // m.put("method", method);
        // m.put("format", format);
        // m.put("charset", charset);
        // m.put("sign_type", sign_type);
        // m.put("timestamp", CharacterString.getCurrentFormatDateTime("yyyy-MM-dd HH:mm:ss"));
        // m.put("version", version);
        // m.put("notify_url", notify_url);
        // m.put("biz_content", biz_content);
        // ArrayList<Entry<String, String>> list = sortParameter(m);
        // String str = createParameterLinkString(list);
        // return str;
        // } catch (Exception e) {
        // Log.logger.error(e.toString());
        // return null;
        // }
        // }

        public static String createAlipayStringForWap(String app_id, String method, String format, String return_url, String charset, String sign_type, String version, String notify_url, String biz_content) throws Exception {
                HashMap<String, String> m = new HashMap<String, String>();
                m.put("app_id", app_id);
                m.put("method", method);
                m.put("format", format);
                m.put("return_url", return_url);
                m.put("charset", charset);
                m.put("sign_type", sign_type);
                m.put("timestamp", CharacterString.getCurrentFormatDateTime("yyyy-MM-dd HH:mm:ss"));
                m.put("version", version);
                m.put("notify_url", notify_url);
                m.put("biz_content", biz_content);
                ArrayList<Entry<String, String>> list = sortParameter(m);
                String str = createParameterLinkString(list);
                return str;
        }

        public static String encryptAlipayStringByRsa(String str) throws Exception {
                String sign = Rsa.sign(str.getBytes(Alipay.CHARSET), PRIVATE_KEY);
                if (null == sign) {
                        return null;
                }
                String s = str + "&sign=" + sign;
                return URLEncoder.encode(s, Alipay.CHARSET);
        }

        // public static String kkk() {
        // String bizContent = createAlipayBizContentForWap("商品的描述信息", "商品的标题", CharacterString.generateUuidStr(true), "24h", "0.01", Alipay.PARTNER, "", Alipay.WAP_PRODUCT_CODE);
        // String str = createAlipayStringForWap(Alipay.APP_ID, Alipay.WAP_METHOD, Alipay.FORMAT, "", Alipay.CHARSET, Alipay.SIGN_TYPE_RSA, Alipay.VERSION, Alipay.NOTIFY_URL, bizContent);
        // str = Alipay.encryptAlipayStringByRsa(str); // RSA
        // return str;
        // }
}