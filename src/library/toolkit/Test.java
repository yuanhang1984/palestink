package library.toolkit;

import library.encrypt.Base64;

public class Test {
        public Test() {
                System.out.println(Base64.encode("helloworld".getBytes()));
        }

        public static void main(String[] args) {
                new Test();
        }
}