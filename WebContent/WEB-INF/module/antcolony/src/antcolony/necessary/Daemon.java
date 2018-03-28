package module.antcolony.necessary;

import javax.servlet.ServletContext;
import framework.sdk.spec.module.necessary.DaemonAction;

public class Daemon extends DaemonAction {
        public static final String MODULE_NAME = "antcolony.Daemon";

        public Daemon(ServletContext servletContext) {
                super(servletContext);
        }

        @Override
        public void run() {
        }
}