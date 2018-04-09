package library.toolkit;

import library.string.CharacterString;

public class Test {
        public Test() {
                try {
                        System.out.println(CharacterString.regularExpressionCheck("^[0-9a-zA-Z_]{1,}\\.java$", "Custom.java"));
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
        }

        public static void main(String[] args) {
                new Test();
        }
}