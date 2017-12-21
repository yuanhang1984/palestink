package library.toolkit;

public class Test {
        public Test() {
                String a = "yuanhang";
                String b = "wangjiajia";
                String c = "yuanruyi";
                byte[] ab = a.getBytes();
                byte[] bb = b.getBytes();
                byte[] cb = c.getBytes();
                StringBuilder sb = new StringBuilder();
                sb.append(new String(ab, 0, ab.length));
                sb.append(new String(bb, 0, bb.length));
                sb.append(new String(cb, 0, cb.length));
                System.out.println(sb.toString());
        }

        public static void main(String[] args) {
                new Test();
        }
}