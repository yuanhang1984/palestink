package library.toolkit;

// import org.json.JSONObject;

// import java.util.HashMap;
// import org.dom4j.Document;
// import org.dom4j.Element;
// import org.dom4j.io.SAXReader;
// import library.database.DatabaseKit;

public class Test {
        public Test() {
                try {
                        String type = "foreach-update>>userList:name,age->myAge,address";
                        if (2 != type.split(">>").length) {
                                System.out.println("11111111111111111111");
                                return;
                        }
                        if (2 != type.split(">>")[1].split(":").length) {
                                System.out.println("22222222222222222222");
                                return;
                        }
                        // 结果集别名
                        String aliasName = type.split(">>")[1].split(":")[0];
                        // 结果集引用数据的参数名
                        String[] parameterList = type.split(">>")[1].split(":")[1].split(",");
                        System.out.println("别名：" + aliasName);
                        for (int i = 0; i < parameterList.length; i++) {
                                System.out.println("参数：" + parameterList[i]);
                        }
                        // // 结果集存放的别名
                        // String aliasName = type.split("->")[1].split("\\|")[0];
                        // // 传入的参数名
                        // String paramName = type.split("->")[1].split("\\|")[1];
                        // ArrayList<Object> al = dataArrayMap.get(aliasName);
                        // Iterator<Object> iter = al.iterator();
                        // while (iter.hasNext()) {
                        // Object obj = iter.next();
                        // p.put(paramName, obj);
                        // int res = s.update(str, p);
                        // if (1 > res) {
                        // s.rollback();
                        // Message.send(request, response, Message.STATUS.ERROR, null, "Transaction (" + str + ") Foreach Update Roll Back");
                        // return;
                        // }
                        // }
                        // JSONObject o = new JSONObject();
                        // o.put("name", JSONObject.NULL);
                        // System.out.println(o.toString());
                        // String xml = "[类型][模块名称][自定义类的调用方法|sql元素id][参数列表];" + System.getProperty("line.separator") + "[类型][模块名称][自定义类的调用方法|sql元素id][参数1,参数2,参数3,参数4,];" + "[custom][fjTrade][lego_storage.Custom.t_checkFilesExist.checkFilesExist][directory_uuid,cluster_list,file_status->status,creator_uuid];" + System.getProperty("line.separator") + "[select][fjTrade][selectVehicleInformation][vehicle_information_uuid->uuid];" + System.getProperty("line.separator") + "[insert][fjTrade][insertBoatUllageReport][uuid,vehicle_information_uuid,quantity,cluster_list];" + System.getProperty("line.separator");
                        // ArrayList<Namespace> list = Namespace.analyseNamespace(xml);
                        // Iterator<Namespace> iter = list.iterator();
                        // System.out.println("共有 " + list.size() + " 条执行命令");
                        // int i = 1;
                        // while (iter.hasNext()) {
                        // System.out.println("第 " + i + " 条");
                        // Namespace ns = iter.next();
                        // System.out.println("类型: " + ns.getType());
                        // System.out.println("模块名称: " + ns.getModuleName());
                        // System.out.println("key: " + ns.getId());
                        // System.out.println("参数列表: ");
                        // for (int n = 0; n < ns.getParameterList().length; n++) {
                        // System.out.println(ns.getParameterList()[n]);
                        // }
                        // i++;
                        // }
                        // String n1 = "A";
                        // String n2 = "A";
                        // int res = n1.compareTo(n2);
                        // if (0 == res) {
                        // System.out.println("相等");
                        // } else if (0 < res) {
                        // System.out.println("n1大于n2");
                        // } else if (0 > res) {
                        // System.out.println("n1小于n2");
                        // }
                        // System.exit(1);
                        // HashMap<String, Object> hm = new HashMap<String, Object>();
                        // hm.put("uuid", "AAAAAAA00000");
                        // hm.put("vehicle_information_uuid", "ayuanhang");
                        // hm.put("offset", 0);
                        // hm.put("rows", 11);
                        // hm.put("quantity", "abc");
                        // hm.put("cluster_list", "a;b;c;");
                        // hm.put("sn_cluster_list", "set_null");
                        // hm.put("address", "qilishan");
                        // hm.put("a1", null);
                        // hm.put("b2", "This is b2");
                        // hm.put("c3", "123");
                        // hm.put("d4", "hello world");
                        // hm.put("name", "yuanhang");
                        // hm.put("age", 33);
                        // hm.put("data_count", 1);
                        // String filePath = "C:\\Users\\Administrator\\Desktop\\tb.xml";
                        // SAXReader reader = new SAXReader();
                        // Document doc = reader.read(filePath);
                        // Element sql = doc.getRootElement();
                        // System.out.println(DatabaseKit.composeSql(sql, "insertBoatUllageReport", hm));
                        // System.out.println(DatabaseKit.composeSql(sql, "deleteBoatUllageReport", hm));
                        // System.out.println(DatabaseKit.composeSql(sql, "updateBoatUllageReport", hm));
                        // System.out.println(DatabaseKit.composeSql(sql, "selectBoatUllageReport", hm));
                        // Element delete = sql.element("delete");
                        // Element where = delete.element("where");
                        // List<?> list = delete.content();
                        // Iterator<?> iter = list.iterator();
                        // while (iter.hasNext()) {
                        // Object obj = iter.next();
                        // System.out.println("==========================");
                        // if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaulttext")) {
                        // DefaultText dt = (DefaultText) obj;
                        // System.out.println("DefaultText");
                        // String a = dt.getText().trim();
                        // System.out.println(a.length());
                        // System.out.println(dt.getText().trim());
                        // } else if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaultcdata")) {
                        // DefaultCDATA dc = (DefaultCDATA) obj;
                        // System.out.println("DefaultCDATA");
                        // System.out.println(dc.getText().trim());
                        // } else if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaultelement")) {
                        // DefaultElement de = (DefaultElement) obj;
                        // System.out.println("DefaultElement");
                        // System.out.println(de.getName().trim());
                        // } else if (-1 != obj.getClass().getName().toLowerCase().indexOf("defaultcomment")) {
                        // DefaultComment dc = (DefaultComment) obj;
                        // continue;
                        // }
                        // // if (n instanceof Element) {
                        // // System.out.println("This is a Element!");
                        // // System.out.println(n);
                        // // } else if (n instanceof org.dom4j.Comment) {
                        // // System.out.println("This is a Comment!");
                        // // System.out.println(n);
                        // // } else {
                        // // System.out.println("This is a Nothing!");
                        // // System.out.println(n);
                        // // }
                        // // if (Node.COMMENT_NODE == n.getNodeType()) {
                        // // System.out.println("======================");
                        // // System.out.println(n.asXML());
                        // // }
                        // // Node node = (Node) iterator.next();
                        // // if (node instanceof Element) {
                        // // Element e = (Element) node;
                        // // System.out.println(e.getTextTrim());
                        // // }
                        // // System.out.println(node.getText());
                        // }
                        // List list = where.content();
                        // Iterator iter = list.iterator();
                        // while (iter.hasNext()) {
                        // DefaultElement obj = (DefaultElement) iter.next();
                        // System.out.println(obj.asXML());
                        // }
                        // System.out.println(where.getStringValue());
                        // List list = delete.content();
                        // Iterator iter = list.iterator();
                        // while (iter.hasNext()) {
                        // DefaultCDATA s = (DefaultCDATA) iter.next();
                        // System.out.println(s);
                        // }
                        // System.out.println(delete.isTextOnly());
                        // setData("1111111222222222233333333");
                        // System.out.println(delete.getTextTrim());
                        // Element where = delete.element("where");
                        // HashMap<String, Object> hm = new HashMap<String, Object>();
                        // // hm.put("uuid", "01234567890");
                        // hm.put("uuid", null);
                        // hm.put("name", "yuanhang");
                        // hm.put("age", 32);
                        // hm.put("address", "qilishan");
                        // ArrayList<String> list = DatabaseKit.composeSqlTagIf(where, hm);
                        // Iterator<String> iter = list.iterator();
                        // while (iter.hasNext()) {
                        // String s = iter.next();
                        // System.out.println(s);
                        // }
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        // Element choose = delete.element("choose");
                        // HashMap<String, Object> hm = new HashMap<String, Object>();
                        // hm.put("uuid", null);
                        // hm.put("name", "yuanhang");
                        // // hm.put("name", null);
                        // hm.put("age", 32);
                        // hm.put("address", "qilishan");
                        // String s = DatabaseKit.composeSqlTagChoose(choose, hm);
                        // System.out.println(s);
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        // HashMap<String, Object> hm = new HashMap<String, Object>();
                        // hm.put("uuid", "0123");
                        // hm.put("vehicle_information_uuid", "234");
                        // hm.put("quantity", 32.12);
                        // // hm.put("cluster_list", "1;2;3;");
                        // String s = DatabaseKit.composeSqlReplaceParameter(delete, hm);
                        // System.out.println(s);
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        ///////////////////////////////////////////
                        // Iterator<?> ifIter = where.elementIterator();
                        // while (ifIter.hasNext()) {
                        // Element sqlElement = (Element) sqlIter.next();
                        // System.out.println("标签: " + sqlElement.getName());
                        // System.out.println("id: " + sqlElement.attributeValue("id"));
                        // System.out.println("description: " + sqlElement.attributeValue("description"));
                        // String cnt = sqlElement.getTextTrim();
                        // System.out.println("内容: " + cnt);
                        // Pattern pattern = Pattern.compile("#\\{(.+?)\\}");
                        // Matcher matcher = pattern.matcher(cnt);
                        // while (matcher.find()) {
                        // System.out.println(matcher.group(1));
                        // }
                        // break;
                        // }
                } catch (Exception e) {
                        System.out.println(e.toString());
                }
        }

        public static void main(String[] args) {
                new Test();
        }
}