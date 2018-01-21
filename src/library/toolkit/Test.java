package library.toolkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Test {
        public Test() {
                String fileName = "1.bmp";
                try {
                        System.out.println(Files.probeContentType(Paths.get(fileName)));
                } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                // 0123456789012345678901234567890123456789012345678
                String id = "module.file_storage.necessary.Custom.downloadFile";
                if (3 > id.split("\\.").length) {
                        System.out.println("11111111111111");
                        return;
                }
                String tmpId = id;
                int methodNameIndex = tmpId.lastIndexOf(".");
                String methodName = tmpId.substring(methodNameIndex + 1);
                tmpId = tmpId.substring(0, methodNameIndex);
                int classNameIndex = tmpId.lastIndexOf(".");
                String className = tmpId.substring(classNameIndex + 1);
                tmpId = tmpId.substring(0, classNameIndex);
                String packageName = tmpId;
                // int classNameIndex = id.lastIndexOf(".", methodNameIndex + 1);
                // String className = id.substring(classNameIndex, methodNameIndex + 1);
                // String packageName = id.substring(0, classNameIndex + 1);
                System.out.println(packageName);
                System.out.println(className);
                System.out.println(methodName);
        }

        public static void main(String[] args) {
                new Test();
        }
}