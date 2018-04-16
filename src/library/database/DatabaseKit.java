package library.database;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dom4j.Element;
import org.dom4j.tree.DefaultText;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultElement;

public class DatabaseKit {
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
                        // 如果没有找到需要替换的变量，直接返回sql语句的文本。
                        return text;
                }
                // 上面find了一次，这里需要重置一下。
                matcher.reset();
                // 循环遍历语句中的每个变量
                while (matcher.find()) {
                        // 如：#{name}获取name。
                        String name = matcher.group(1);
                        // 从参数中获取name所指的参数值
                        Object value = parameter.get(name);
                        if (null == value) {
                                // 如果没有找到参数值，说明未设置参数。
                                continue;
                        }
                        // sql中数字类型的可以直接加入sql语句，但是字符串等类型需要增加单引号。
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
                        // 替换语句中的变量
                        text = text.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                }
                // 返回sql语句
                return text;
        }

        /**
         * 组成Sql的choose标签（一个choose下面，只有一对if和else标签。相互之间不能嵌套。）
         * @param chooseElement choose标签
         * @param parameter 传入参数
         * @return 符合条件的sql语句
         */
        private static String composeSqlTagChoose(Element chooseElement, HashMap<String, Object> parameter) {
                Element ifElement = chooseElement.element("if");
                Element elseElement = chooseElement.element("else");
                String parameterName = ifElement.attributeValue("parameterName");
                String parameterValue = ifElement.attributeValue("parameterValue");
                String operatorType = ifElement.attributeValue("operatorType");
                Object obj = parameter.get(parameterName);
                String text = "";
                if ((parameterValue.equalsIgnoreCase("#null")) && (operatorType.equalsIgnoreCase("equal"))) {// 内置常量#null
                        if (null != obj) {
                                // 不满足if条件，赋予else内容。
                                text = elseElement.getTextTrim();
                        } else {
                                text = ifElement.getTextTrim();
                        }
                } else if ((parameterValue.equalsIgnoreCase("#null")) && (operatorType.equalsIgnoreCase("unequal"))) {// 内置常量#null
                        if (null == obj) {
                                // 不满足if条件，赋予else内容。
                                text = elseElement.getTextTrim();
                        } else {
                                text = ifElement.getTextTrim();
                        }
                } else {// 其他固定常量
                        if (null == obj) {
                                // 如果obj为空，说明传入的参数中没有这个值，那么自然失去了if的判断依据，所以应该等于else的内容。
                                text = elseElement.getTextTrim();
                        } else {
                                int res = parameterValue.toLowerCase().compareTo(obj.toString().toLowerCase());
                                if (operatorType.equalsIgnoreCase("equal")) {// 等于
                                        if (0 == res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                text = elseElement.getTextTrim();
                                        }
                                } else if (operatorType.equalsIgnoreCase("unequal")) {// 不等于
                                        if (0 != res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                text = elseElement.getTextTrim();
                                        }
                                } else if (operatorType.equalsIgnoreCase("greater")) {// 大于
                                        if (0 > res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                text = elseElement.getTextTrim();
                                        }
                                } else if (operatorType.equalsIgnoreCase("less")) {// 小于
                                        if (0 < res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                text = elseElement.getTextTrim();
                                        }
                                }
                        }
                }
                Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
                Matcher matcher = pattern.matcher(text);
                if (!matcher.find()) {
                        // 如果没有找到需要替换的变量，直接返回sql语句的文本。
                        return text;
                }
                // 上面find了一次，这里需要重置一下。
                matcher.reset();
                // 循环遍历语句中的每个变量
                while (matcher.find()) {
                        // 如：#{name}获取name。
                        String name = matcher.group(1);
                        // 从参数中获取name所指的参数值
                        Object value = parameter.get(name);
                        if (null == value) {
                                // 如果没有找到参数值，说明未设置参数。
                                continue;
                        }
                        // sql中数字类型的可以直接加入sql语句，但是字符串等类型需要增加单引号。
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
                        // 替换语句中的变量
                        text = text.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                }
                // 返回sql语句
                return text;
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
                String operatorType = ifElement.attributeValue("operatorType");
                Object obj = parameter.get(parameterName);
                String text = "";
                if ((parameterValue.equalsIgnoreCase("#null")) && (operatorType.equalsIgnoreCase("equal"))) {// 内置常量#null
                        if (null != obj) {
                                // 不满足if条件，返回空字符串。
                                return "";
                        }
                        text = ifElement.getTextTrim();
                } else if ((parameterValue.equalsIgnoreCase("#null")) && (operatorType.equalsIgnoreCase("unequal"))) {// 内置常量#null
                        if (null == obj) {
                                // 不满足if条件，返回空字符串。
                                return "";
                        }
                        text = ifElement.getTextTrim();
                } else {// 其他固定常量
                        if (null == obj) {
                                // 如果obj为空，说明传入的参数中没有这个值，那么自然失去了if的判断依据，所以应该返回空字符串。
                                return "";
                        }
                        int res = parameterValue.toLowerCase().compareTo(obj.toString().toLowerCase());
                        if (operatorType.equalsIgnoreCase("equal")) {// 等于
                                if (0 == res) {
                                        text = ifElement.getTextTrim();
                                } else {
                                        return "";
                                }
                        } else if (operatorType.equalsIgnoreCase("unequal")) {// 不等于
                                if (0 != res) {
                                        text = ifElement.getTextTrim();
                                } else {
                                        return "";
                                }
                        } else if (operatorType.equalsIgnoreCase("greater")) {// 大于
                                if (0 > res) {
                                        text = ifElement.getTextTrim();
                                } else {
                                        return "";
                                }
                        } else if (operatorType.equalsIgnoreCase("less")) {// 小于
                                if (0 < res) {
                                        text = ifElement.getTextTrim();
                                } else {
                                        return "";
                                }
                        }
                }
                Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
                Matcher matcher = pattern.matcher(text);
                if (!matcher.find()) {
                        // 如果没有找到需要替换的变量，直接返回sql语句的文本。
                        return ifElement.getTextTrim();
                }
                // 上面find了一次，这里需要重置一下。
                matcher.reset();
                // 循环遍历语句中的每个变量
                while (matcher.find()) {
                        // 如：#{name}获取name。
                        String name = matcher.group(1);
                        // 从参数中获取name所指的参数值
                        Object value = parameter.get(name);
                        if (null == value) {
                                // 如果没有找到参数值，说明未设置参数。
                                continue;
                        }
                        // sql中数字类型的可以直接加入sql语句，但是字符串等类型需要增加单引号。
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
                        // 替换语句中的变量
                        text = text.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                }
                // 返回sql语句
                return text;
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
                        String operatorType = ifElement.attributeValue("operatorType");
                        Object obj = parameter.get(parameterName);
                        String text = "";
                        if ((parameterValue.equalsIgnoreCase("#null")) && (operatorType.equalsIgnoreCase("equal"))) {// 内置常量#null
                                if (null != obj) {
                                        // 不满足if条件，返回空字符串。
                                        continue;
                                }
                                text = ifElement.getTextTrim();
                        } else if ((parameterValue.equalsIgnoreCase("#null")) & (operatorType.equalsIgnoreCase("unequal"))) {// 内置常量#null
                                if (null == obj) {
                                        // 不满足if条件，返回空字符串。
                                        continue;
                                }
                                text = ifElement.getTextTrim();
                        } else {// 其他固定常量
                                if (null == obj) {
                                        // 如果obj为空，说明传入的参数中没有这个值，那么自然失去了if的判断依据，所以应该继续遍历。
                                        continue;
                                }
                                int res = parameterValue.toLowerCase().compareTo(obj.toString().toLowerCase());
                                if (operatorType.equalsIgnoreCase("equal")) {// 等于
                                        if (0 == res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                continue;
                                        }
                                } else if (operatorType.equalsIgnoreCase("unequal")) {// 不等于
                                        if (0 != res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                continue;
                                        }
                                } else if (operatorType.equalsIgnoreCase("greater")) {// 大于
                                        if (0 > res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                continue;
                                        }
                                } else if (operatorType.equalsIgnoreCase("less")) {// 小于
                                        if (0 < res) {
                                                text = ifElement.getTextTrim();
                                        } else {
                                                continue;
                                        }
                                }
                        }
                        Pattern pattern = Pattern.compile("\\#\\{(.+?)\\}");
                        Matcher matcher = pattern.matcher(text);
                        if (!matcher.find()) {
                                // 如果没有找到需要替换的变量，直接返回sql语句的文本。
                                list.add(ifElement.getTextTrim());
                                continue;
                        }
                        // 上面find了一次，这里需要重置一下。
                        matcher.reset();
                        // 循环遍历语句中的每个变量
                        while (matcher.find()) {
                                // 如：#{name}获取name。
                                String name = matcher.group(1);
                                // 从参数中获取name所指的参数值
                                Object value = parameter.get(name);
                                if (null == value) {
                                        // 如果没有找到参数值，说明未设置参数。
                                        continue;
                                }
                                // sql中数字类型的可以直接加入sql语句，但是字符串等类型需要增加单引号。
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
                                // 替换语句中的变量
                                text = text.replaceAll("\\#\\{" + name + "\\}", parameterStr);
                                list.add(text);
                        }
                }
                // 返回sql语句集合
                return list;
        }

        /**
         * 组合Sql
         * @param sqlRoot sql元素
         * @param id sql中的元素节点
         * @param parameter 参数列表
         * @return sql
         */
        public static String composeSql(Element sqlRoot, String id, HashMap<String, Object> parameter) {
                String s = "";
                Iterator<?> sqlRootIter = sqlRoot.elementIterator();
                Element targetElement = null;
                // 根据elementId遍历查询元素
                while (sqlRootIter.hasNext()) {
                        Element e = (Element) sqlRootIter.next();
                        if (e.attributeValue("id").equalsIgnoreCase(id)) {
                                targetElement = e;
                                break;
                        }
                }
                // 如果没有找到元素，返回null。
                if (null == targetElement) {
                        return "";
                }
                // 遍历elementId下所有的节点
                List<?> list = targetElement.content();
                Iterator<?> iter = list.iterator();
                while (iter.hasNext()) {
                        Object obj = iter.next();
                        if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaulttext")) {// defaulttext与defaultcdata功能相同
                                DefaultText dt = (DefaultText) obj;
                                String text = dt.getText().trim();
                                if (0 == text.length()) {
                                        continue;
                                }
                                s += (" " + DatabaseKit.composeSqlReplaceParameter(text, parameter));
                        } else if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaultcdata")) {// defaulttext与defaultcdata功能相同
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
                                        String prefix = "";
                                        Pattern pattern = null;
                                        Matcher matcher = null;
                                        pattern = Pattern.compile("^and\\s+");
                                        matcher = pattern.matcher(str);
                                        if (matcher.find()) {
                                                // and连接
                                                prefix = "and ";
                                                if (str.toLowerCase().startsWith(prefix)) {
                                                        str = str.substring(prefix.length());
                                                }
                                                if (0 < str.trim().length()) {
                                                        s += (" where" + " " + str);
                                                }
                                                continue;
                                        }
                                        pattern = Pattern.compile("^or\\s+");
                                        matcher = pattern.matcher(str);
                                        if (matcher.find()) {
                                                // and连接
                                                prefix = "or ";
                                                if (str.toLowerCase().startsWith(prefix)) {
                                                        str = str.substring(prefix.length());
                                                }
                                                if (0 < str.trim().length()) {
                                                        s += (" where" + " " + str);
                                                }
                                                continue;
                                        }

                                } else if (name.equalsIgnoreCase("set")) {
                                        Iterator<String> i = DatabaseKit.composeSqlTagIfByParent(de, parameter).iterator();
                                        String str = "";
                                        while (i.hasNext()) {
                                                str += (i.next() + " ");
                                        }
                                        if (0 < str.trim().length()) {
                                                str = str.trim();
                                                if ((str.lastIndexOf(",") + 1) == str.length()) {
                                                        str = str.substring(0, str.length() - 1);
                                                }
                                                s += (" set" + " " + str);
                                        }
                                } else if (name.equalsIgnoreCase("choose")) {
                                        s += DatabaseKit.composeSqlTagChoose(de, parameter);
                                } else if (name.equalsIgnoreCase("if")) {
                                        String str = DatabaseKit.composeSqlTagIfBySelf(de, parameter).trim();
                                        s += (" " + str);
                                }
                        }
                }
                // 如果sql中依旧有“变量”，说明该变量至允许为空，这里置为null。
                s = s.replaceAll("\\#\\{.+?\\}", "null");
                return s.trim();
        }
}