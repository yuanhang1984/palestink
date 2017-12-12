package library.string;

import java.util.UUID;
import java.util.Random;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;

/**
 * 字符串操作
 */
public class CharacterString {
        /**
         * 返回uuid字符串
         * 
         * @param removeLine 如果为true，返回不含中划线的uuid；如果为false，返回带有中划线的uuid。
         * @return uuid字符串
         */
        public static String getUuidStr(boolean removeLine) {
                if (removeLine) {
                        return UUID.randomUUID().toString().replaceAll("-", "");
                } else {
                        return UUID.randomUUID().toString();
                }
        }

        /**
         * 正则表达式检查
         * 
         * @param regex 正则表达式
         * @param str 待检查的内容
         * @return 如果内容匹配正则表达式，返回true；如果内容不匹配，返回false。
         */
        public static boolean regularExpressionCheck(String regex, String str) {
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(str);
                return m.matches();
        }

        /**
         * 字符串首字母大写转换（注意：字符串长度的合法性在调用这个方法前检查）
         * 
         * @param s 待转换的字符串
         * @return 首字母转换后的字符串
         */
        public static String stringFirstCharUpperCase(String s) {
                return String.valueOf(s.charAt(0)).toUpperCase() + s.substring(1);
        }

        /**
         * 根据参数指定的长度，返回随机数字组成的字符串。
         * 
         * @param length 指定的随机字符串长度
         * @return 随机字符串
         */
        public static String generateRandomNum(int length) {
                String num = "";
                Random r = new Random();
                for (int i = 0; i < length; i++) {
                        num += r.nextInt(10);
                }
                return num;
        }

        /**
         * 获取指定时间格式当前时间的字符串。<br />
         * 格式如下：年（yyyy）、月（MM）、日（dd）、时（HH）、分（mm）、秒（ss）、毫秒（SSS）。<br />
         * 常用格式有：yyyy-MM-dd HH:mm:ss
         * 
         * @param format 时间格式
         * @return 指定格式下当前时间的字符串
         */
        public static String getCurrentFormatDateTime(String format) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat f = new SimpleDateFormat(format);
                return f.format(cal.getTime());
        }

        /**
         * 按照“秒”的设置，获取自定义格式的时间字符串。<br />
         * 可用于“以秒递增”实现音频时间轴的增加<br >
         * 
         * @param format 时间格式
         * @param second 秒
         * @return 指定格式下的时间字符串
         */
        public static String getCustomFormatDateTimeBySecond(String format, int second) throws Exception {
                Calendar cal = Calendar.getInstance();
                cal.setTime(new SimpleDateFormat("ss").parse(String.valueOf(second)));
                SimpleDateFormat f = new SimpleDateFormat(format);
                return f.format(cal.getTime());
        }

        /**
         * 获取字符串的拼音
         * 
         * @param str 汉字文本
         * @return 拼音文本
         */
        public static String getPinYin(String str) throws Exception {
                String res = "";
                char[] c = null;
                c = str.toCharArray();
                String[] s = new String[c.length];
                HanyuPinyinOutputFormat hpof = new HanyuPinyinOutputFormat();
                hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);
                hpof.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                hpof.setVCharType(HanyuPinyinVCharType.WITH_V);
                for (int i = 0; i < c.length; i++) {
                        /*
                         * 判断是否为汉字字符
                         */
                        if (java.lang.Character.toString(c[i]).matches("[\\u4E00-\\u9FA5]+")) {
                                s = PinyinHelper.toHanyuPinyinStringArray(c[i], hpof);
                                res += s[0];
                        } else {
                                res += java.lang.Character.toString(c[i]);
                        }
                }
                return res;
        }

        /**
         * 获取字符串拼音的缩写
         * 
         * @param str 汉字文本
         * @param isUpper 是否转换大写（true：返回结果大写；false：返回结果小写）
         * @return 拼音缩写文本
         */
        public static String getPinYinShort(String str, boolean isUpper) throws Exception {
                String res = "";
                for (int i = 0; i < str.length(); i++) {
                        if (isUpper) {
                                res += String.valueOf(CharacterString.getPinYin(String.valueOf(str.charAt(i))).charAt(0)).toUpperCase();
                        } else {
                                res += String.valueOf(CharacterString.getPinYin(String.valueOf(str.charAt(i))).charAt(0)).toLowerCase();
                        }
                }
                return res;
        }
}