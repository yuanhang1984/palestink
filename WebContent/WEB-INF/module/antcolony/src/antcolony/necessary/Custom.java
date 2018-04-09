package module.antcolony.necessary;

import java.util.HashMap;
import java.sql.Connection;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;

import framework.sdk.msg.Message;
import framework.sdk.spec.module.necessary.CustomAction;
import module.antcolony.optional.Module;

public class Custom extends CustomAction {
        private HttpServlet httpServlet;
        private HttpServletRequest httpServletRequest;
        private HttpServletResponse httpServletResponse;
        private HashMap<String, Object> parameter;
        private Connection connection;
        private Module module;

        public Custom(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                this.httpServlet = httpServlet;
                this.httpServletRequest = httpServletRequest;
                this.httpServletResponse = httpServletResponse;
                this.parameter = parameter;
                this.connection = connection;
                this.module = new Module(this.httpServlet, this.httpServletRequest, this.httpServletResponse, this.connection, this.parameter);
        }

        public Message getLoadNameList() {
                return this.module.getLoadNameList();
        }

        public Message getDiskNameList() {
                return this.module.getDiskNameList();
        }

        public Message getPermissionList() {
                return this.module.getPermissionList();
        }

        public Message addModule() {
                return this.module.addModule();
        }

        public Message removeModule() {
                return this.module.removeModule();
        }

        public Message modifyModuleName() {
                return this.module.modifyModuleName();
        }

        public Message startWebServer() {
                return this.module.startWebServer();
        }

        public Message stopWebServer() {
                return this.module.stopWebServer();
        }

        public Message readServerLogFile() {
                return this.module.readServerLogFile();
        }

        public Message readServerResourceFile() {
                this.module.getParameter().put("directory", "res");
                return this.module.readServerFile();
        }

        public Message downloadServerResourceFile() {
                this.module.getParameter().put("directory", "res");
                return this.module.downloadServerFile();
        }

        public Message uploadServerResourceFile() {
                this.module.getParameter().put("directory", "res");
                return this.module.uploadServerFile();
        }

        public Message uploadServerSourceCode() {
                this.module.getParameter().put("directory", "src");
                FileItem attachment = (FileItem) this.module.getParameter().get("attachment");
                String fileName = this.module.getParameter().get("moduleName") + "/" + this.module.getParameter().get("type") + "/" + attachment.getName();
                this.module.getParameter().put("fileName", fileName);
                return this.module.uploadServerFile();
        }

        public Message removeServerSourceCode() {
                this.module.getParameter().put("directory", "src");
                String fileName = this.module.getParameter().get("moduleName") + "/" + this.module.getParameter().get("type") + "/" + this.module.getParameter().get("fileName");
                this.module.getParameter().put("fileName", fileName);
                return this.module.removeServerFile();
        }

        public Message downloadServerSourceFile() {
                this.module.getParameter().put("directory", "src");
                String fileName = this.module.getParameter().get("moduleName") + "/" + this.module.getParameter().get("type") + "/" + this.module.getParameter().get("fileName");
                this.module.getParameter().put("fileName", fileName);
                return this.module.downloadServerFile();
        }

        public Message uploadServerSourceLibrary() {
                FileItem attachment = (FileItem) this.module.getParameter().get("attachment");
                this.module.getParameter().put("directory", "lib");
                this.module.getParameter().put("fileName", attachment.getName());
                return this.module.uploadServerFile();
        }

        public Message removeServerSourceLibrary() {
                this.module.getParameter().put("directory", "lib");
                return this.module.removeServerFile();
        }

        public Message downloadServerSourceLibrary() {
                this.module.getParameter().put("directory", "lib");
                return this.module.downloadServerFile();
        }

        public Message getServerSourceFileList() {
                return this.module.getServerSourceFileList();
        }

        public Message getServerLibraryFileList() {
                return this.module.getServerLibraryFileList();
        }
}