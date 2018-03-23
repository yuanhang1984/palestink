package library.toolkit;

import org.json.JSONObject;

import framework.sdk.msg.Message;

public class Test {
        public Test() {
                JSONObject o = new JSONObject();
                o.put("result", Message.SIGN.ALREADY_FEEDBACK_TO_CLIENT);
                System.out.println(o.toString());
        }

        public static void main(String[] args) {
                new Test();
        }
}