package library.database;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Element;
import org.dom4j.tree.DefaultText;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultElement;

public class DatabaseKit {
        /**
         * 是否存在数据
         * 
         * @param list
         * @return true:存有数据;false:没有数据。
         */
        public static boolean hasData(List<?> list) {
                if (null == list) {
                        return false;
                }
                if (0 >= list.size()) {
                        return false;
                }
                return true;
        }

        /**
         * 组成Sql的直接替换变量
         * @param text sql语句文本
         * @param parameter 传入参数
         * @return 组合的Sql
         */
        private static String composeSqlReplaceParameter(String text, HashMap<String, Object> parameter) {
                Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
                Matcher matcher = pattern.matcher(text);
                if (!matcher.find()) {
                        return text;
                }
                matcher.reset();
                while (matcher.find()) {
                        String name = matcher.group(1);
                        Object value = parameter.get(name);
                        if (null == value) {
                                continue;
                        }
                        String parameterStr = "";
                        if (-1 != value.getClass().getName().toLowerCase().indexOf("integer")) {
                                parameterStr = String.valueOf(value);
                        } else if (-1 != value.getClass().getName().toLowerCase().indexOf("float")) {
                                parameterStr = String.valueOf(value);
                        } else if (-1 != value.getClass().getName().toLowerCase().indexOf("double")) {
                                parameterStr = String.valueOf(value);
                        } else {
                                parameterStr = "'" + String.valueOf(value) + "'";
                        }
                        text = text.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                }
                return text;
        }

        /**
         * 组成Sql的choose标签（一个choose下面，只有一对if和else标签。相互之间不能嵌套。）
         * @param chooseElement choose标签
         * @param parameter 传入参数
         * @return 符合if条件的sql语句
         */
        private static String composeSqlTagChoose(Element chooseElement, HashMap<String, Object> parameter) {
                Element ifElement = chooseElement.element("if");
                Element elseElement = chooseElement.element("else");
                String parameterName = ifElement.attributeValue("parameterName");
                String parameterValue = ifElement.attributeValue("parameterValue");
                Object obj = parameter.get(parameterName);
                if (parameterValue.equalsIgnoreCase("#null")) {// 内置常量#null
                        // 等于NULL
                        if (null != obj) {
                                // 不满足条件继续遍历
                                return elseElement.getTextTrim();
                        }
                } else if (parameterValue.equalsIgnoreCase("#not_null")) {// 内置常量#not_null
                        // 不等于NULL
                        if (null == obj) {
                                // 不满足条件继续遍历
                                return elseElement.getTextTrim();
                        }
                } else {// 其他固定常量
                        if (!(obj.toString()).equalsIgnoreCase(parameterValue)) {
                                // 不满足条件继续遍历
                                return elseElement.getTextTrim();
                        }
                }
                String ifText = ifElement.getTextTrim();
                Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
                Matcher matcher = pattern.matcher(ifText);
                if (!matcher.find()) {
                        return ifText;
                }
                matcher.reset();
                while (matcher.find()) {
                        String name = matcher.group(1);
                        Object value = parameter.get(name);
                        if (null == value) {
                                continue;
                        }
                        String parameterStr = "";
                        if (-1 != value.getClass().getName().toLowerCase().indexOf("integer")) {
                                parameterStr = String.valueOf(value);
                        } else if (-1 != value.getClass().getName().toLowerCase().indexOf("float")) {
                                parameterStr = String.valueOf(value);
                        } else if (-1 != value.getClass().getName().toLowerCase().indexOf("double")) {
                                parameterStr = String.valueOf(value);
                        } else {
                                parameterStr = "'" + String.valueOf(value) + "'";
                        }
                        ifText = ifText.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                }
                return ifText;
        }

        /**
         * 组成Sql的if标签
         * @param ifElement if标签
         * @param parameter 传入参数
         * @return 符合if条件的sql语句的字符串
         */
        private static String composeSqlTagIfBySelf(Element ifElement, HashMap<String, Object> parameter) {
                String parameterName = ifElement.attributeValue("parameterName");
                String parameterValue = ifElement.attributeValue("parameterValue");
                Object obj = parameter.get(parameterName);
                if (parameterValue.equalsIgnoreCase("#null")) {// 内置常量#null
                        // 等于NULL
                        if (null != obj) {
                                // 不满足条件
                                return null;
                        }
                } else if (parameterValue.equalsIgnoreCase("#not_null")) {// 内置常量#not_null
                        // 不等于NULL
                        if (null == obj) {
                                // 不满足条件
                                return null;
                        }
                } else {// 其他固定常量
                        if (null == obj) {
                                // 不满足条件
                                return null;
                        }
                        if (!(obj.toString()).equalsIgnoreCase(parameterValue)) {
                                // 不满足条件
                                return null;
                        }
                }
                String ifText = ifElement.getTextTrim();
                Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
                Matcher matcher = pattern.matcher(ifText);
                if (!matcher.find()) {
                        // 说明传入参数中没有sql所需参数
                        return ifText;
                }
                matcher.reset();
                while (matcher.find()) {
                        String name = matcher.group(1);
                        Object value = parameter.get(name);
                        if (null == value) {
                                // 说明传入参数中没有sql所需参数
                                return ifText;
                        }
                        String parameterStr = "";
                        if (-1 != value.getClass().getName().toLowerCase().indexOf("integer")) {
                                parameterStr = String.valueOf(value);
                        } else if (-1 != value.getClass().getName().toLowerCase().indexOf("float")) {
                                parameterStr = String.valueOf(value);
                        } else if (-1 != value.getClass().getName().toLowerCase().indexOf("double")) {
                                parameterStr = String.valueOf(value);
                        } else {
                                parameterStr = "'" + String.valueOf(value) + "'";
                        }
                        ifText = ifText.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                        return ifText;
                }
                return ifText;
        }

        /**
         * 组成Sql的if标签
         * @param ifParentElement if的父级标签
         * @param parameter 传入参数
         * @return 符合if条件的sql语句的ArrayList集合
         */
        private static ArrayList<String> composeSqlTagIfByParent(Element ifParentElement, HashMap<String, Object> parameter) {
                ArrayList<String> list = new ArrayList<String>();
                Iterator<?> ifIter = ifParentElement.elements("if").iterator();
                while (ifIter.hasNext()) {
                        Element ifElement = (Element) ifIter.next();
                        String parameterName = ifElement.attributeValue("parameterName");
                        String parameterValue = ifElement.attributeValue("parameterValue");
                        Object obj = parameter.get(parameterName);
                        if (parameterValue.equalsIgnoreCase("#null")) {// 内置常量#null
                                // 等于NULL
                                if (null != obj) {
                                        // 不满足条件继续遍历
                                        continue;
                                }
                        } else if (parameterValue.equalsIgnoreCase("#not_null")) {// 内置常量#not_null
                                // 不等于NULL
                                if (null == obj) {
                                        // 不满足条件继续遍历
                                        continue;
                                }
                        } else {// 其他固定常量
                                if (null == obj) {
                                        // 不满足条件继续遍历
                                        continue;
                                }
                                if (!(obj.toString()).equalsIgnoreCase(parameterValue)) {
                                        // 不满足条件继续遍历
                                        continue;
                                }
                        }
                        String ifText = ifElement.getTextTrim();
                        Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
                        Matcher matcher = pattern.matcher(ifText);
                        if (!matcher.find()) {
                                // 说明传入参数中没有sql所需参数
                                list.add(ifText);
                        }
                        matcher.reset();
                        while (matcher.find()) {
                                String name = matcher.group(1);
                                Object value = parameter.get(name);
                                if (null == value) {
                                        // 说明传入参数中没有sql所需参数
                                        list.add(ifText);
                                        continue;
                                }
                                String parameterStr = "";
                                if (-1 != value.getClass().getName().toLowerCase().indexOf("integer")) {
                                        parameterStr = String.valueOf(value);
                                } else if (-1 != value.getClass().getName().toLowerCase().indexOf("float")) {
                                        parameterStr = String.valueOf(value);
                                } else if (-1 != value.getClass().getName().toLowerCase().indexOf("double")) {
                                        parameterStr = String.valueOf(value);
                                } else {
                                        parameterStr = "'" + String.valueOf(value) + "'";
                                }
                                ifText = ifText.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                                list.add(ifText);
                        }
                }
                return list;
        }

        /**
         * 组合Sql
         * @param element sql中的元素节点
         * @param parameter 参数列表
         * @return sql
         */
        public static String composeSql(Element sqlElement, String elementId, HashMap<String, Object> parameter) {
                String s = "";
                Iterator<?> sqlIter = sqlElement.elementIterator();
                Element targetElement = null;
                while (sqlIter.hasNext()) {
                        Element e = (Element) sqlIter.next();
                        if (e.attributeValue("id").equalsIgnoreCase(elementId)) {
                                targetElement = e;
                                break;
                        }
                }
                if (null == targetElement) {
                        return null;
                }
                List<?> list = targetElement.content();
                Iterator<?> iter = list.iterator();
                while (iter.hasNext()) {
                        Object obj = iter.next();
                        if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaulttext")) {
                                DefaultText dt = (DefaultText) obj;
                                String text = dt.getText().trim();
                                if (0 == text.length()) {
                                        continue;
                                }
                                s += (" " + DatabaseKit.composeSqlReplaceParameter(text, parameter));
                        } else if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaultcdata")) {
                                DefaultCDATA dc = (DefaultCDATA) obj;
                                String text = dc.getText().trim();
                                if (0 == text.length()) {
                                        continue;
                                }
                                s += (" " + DatabaseKit.composeSqlReplaceParameter(text, parameter));
                        } else if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaultelement")) {
                                DefaultElement de = (DefaultElement) obj;
                                String name = de.getName();
                                if (name.equalsIgnoreCase("where")) {
                                        Iterator<String> i = DatabaseKit.composeSqlTagIfByParent(de, parameter).iterator();
                                        String str = "";
                                        while (i.hasNext()) {
                                                str += (i.next() + " ");
                                        }
                                        str = str.trim();
                                        String prefix = "and ";
                                        if (str.toLowerCase().startsWith(prefix)) {
                                                str = str.substring(prefix.length());
                                        }
                                        s += (" where" + " " + str);
                                } else if (name.equalsIgnoreCase("set")) {
                                        Iterator<String> i = DatabaseKit.composeSqlTagIfByParent(de, parameter).iterator();
                                        String str = "";
                                        while (i.hasNext()) {
                                                str += (i.next() + " ");
                                        }
                                        str = str.trim();
                                        if ((str.lastIndexOf(",") + 1) == str.length()) {
                                                str = str.substring(0, str.length() - 1);
                                        }
                                        s += (" set" + " " + str);
                                } else if (name.equalsIgnoreCase("choose")) {
                                        s += DatabaseKit.composeSqlTagChoose(de, parameter);
                                } else if (name.equalsIgnoreCase("if")) {
                                        String str = DatabaseKit.composeSqlTagIfBySelf(de, parameter).trim();
                                        s += (" " + str);
                                }
                        }
                }
                return s.trim();
        }

        /**
        * 生成Sql
        * @param sql sql语句（带参数标记）
        * @param parameter （参数）
        * @return sql
        */
        public static String generateSql(String sql, HashMap<String, Object> parameter) {
                String s = sql;
                Iterator<Entry<String, Object>> iter = parameter.entrySet().iterator();
                while (iter.hasNext()) {
                        Entry<String, Object> e = iter.next();
                        String key = e.getKey();
                        // 转义字符的正则替换
                        key = key.replaceAll("\\#", "\\\\#");
                        key = key.replaceAll("\\$", "\\\\$");
                        key = key.replaceAll("\\{", "\\\\{");
                        key = key.replaceAll("\\}", "\\\\}");
                        s = s.replaceAll(key, (String) e.getValue());
                }
                return s;
        }
}