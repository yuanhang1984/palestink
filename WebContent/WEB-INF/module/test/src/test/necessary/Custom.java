package module.test.necessary;

import java.util.HashMap;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.CustomAction;

public class Custom extends CustomAction {
        private HashMap<String, Object> parameter;

        public Custom(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                this.parameter = parameter;
        }

        public Message getName() {
                Message msg = new Message();
                msg.setStatus(Message.STATUS.SUCCESS);
                msg.setError(Message.ERROR.NONE);
                msg.setDetail(this.parameter.get("name"));
                return msg;
        }
}