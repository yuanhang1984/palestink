package library.toolkit;

import java.io.File;
import java.io.FileWriter;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class Test {
        public Test() {
                Document doc = DocumentHelper.createDocument();
                doc.addDocType("xml", null, null);
                Element root = doc.addElement("root");
                root.addComment("This is a test for dom4j, holen, 2004.9.11");
                Element bookElement = root.addElement("book");
                bookElement.addAttribute("show", "yes");
                Element titleElement = bookElement.addElement("title");
                titleElement.setText("Dom4j Tutorials");
                bookElement = root.addElement("book");
                bookElement.addAttribute("show", "yes");
                titleElement = bookElement.addElement("title");
                titleElement.setText("Lucene Studing");
                bookElement = root.addElement("book");
                bookElement.addAttribute("show", "no");
                titleElement = bookElement.addElement("title");
                titleElement.setText("Lucene in Action");
                Element ownerElement = root.addElement("owner");
                ownerElement.setText("O'Reilly");
                try {
                        OutputFormat xmlFormat = new OutputFormat();
                        xmlFormat.setEncoding("UTF-8");
                        // 设置换行
                        xmlFormat.setNewlines(true);
                        // 生成缩进
                        xmlFormat.setIndent(true);
                        // 使用4个空格进行缩进, 可以兼容文本编辑器
                        xmlFormat.setIndent("    ");
                        /** 将document中的内容写入文件中 */
                        XMLWriter writer = new XMLWriter(new FileWriter(new File("e:\\tmp\\1.xml")), xmlFormat);
                        writer.write(doc);
                        writer.close();
                } catch (Exception ex) {
                        ex.printStackTrace();
                }
        }

        public static void main(String[] args) {
                new Test();
        }
}