package module.test.necessary;

import javax.servlet.ServletContext;
import framework.sdk.spec.module.necessary.DaemonAction;

public class Daemon extends DaemonAction {
        public static final String MODULE_NAME = "test.Daemon";

        public Daemon(ServletContext servletContext) {
                super(servletContext);
        }

        @Override
        public void run() {
        }
}