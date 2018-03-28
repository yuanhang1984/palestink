package library.toolkit;

public class Test {
        public Test() {
                try {
                        if (library.string.CharacterString.regularExpressionCheck("^(sql|dispatch)$", "ispatch")) {
                                System.out.println("ok");
                        } else {
                                System.out.println("error");
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public static void main(String[] args) {
                new Test();
        }
}