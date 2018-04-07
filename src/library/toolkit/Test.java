package library.toolkit;

import library.io.InputOutput;

public class Test {
        public Test() {
                try {
                        InputOutput.decompressZipFile("E:\\tmp\\11122\\Desktop.zip", "E:\\tmp\\11122");
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        public static void main(String[] args) {
                new Test();
        }
}