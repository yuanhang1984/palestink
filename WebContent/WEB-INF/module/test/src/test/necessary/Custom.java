package module.test.necessary;

import java.util.HashMap;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import framework.sdk.msg.Message;
import framework.sdk.annotation.Class;
import framework.sdk.annotation.Method;
import framework.sdk.annotation.Parameter;
import framework.sdk.spec.module.necessary.CustomAction;

// @Class(description = "这是一个测试类")
public class Custom extends CustomAction {
        private HashMap<String, Object> parameter;

        public Custom(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                this.parameter = parameter;
        }

        @Method(description = "获取用户名", parameters = { @Parameter(name = "address", description = "详细地址", format = "^.[1,16]$", type = "string"), @Parameter(name = "age", description = "年龄", format = "^\\d$", type = "integer") })
        public Message getName() {
                Message msg = new Message();
                msg.setStatus(Message.STATUS.SUCCESS);
                msg.setError(Message.ERROR.NONE);
                msg.setDetail(this.parameter.get("name"));
                return msg;
        }

        @Method(description = "获取用户年龄", parameters = { @Parameter(name = "address", description = "详细地址", format = "^.[1,16]$", type = "string"), @Parameter(name = "age", description = "年龄", format = "^\\d$", type = "integer") })
        public Message getAge() {
                Message msg = new Message();
                msg.setStatus(Message.STATUS.SUCCESS);
                msg.setError(Message.ERROR.NONE);
                msg.setDetail(this.parameter.get("name"));
                return msg;
        }
}