package library.toolkit;

public class Test {
        public Test() {
                // if (library.string.CharacterString.regularExpressionCheck("^([0-9a-zA-Z]+[.]+[0-9a-zA-Z]+;)+$", "user_security.removeRole;")) {
                if (library.string.CharacterString.regularExpressionCheck("^([0-9a-zA-Z_\\-\\.]{3,};)+$", "user_security.removeRole;")) {
                        System.out.println("ok");
                } else {
                        System.out.println("error");
                }
        }

        public static void main(String[] args) {
                new Test();
        }
}