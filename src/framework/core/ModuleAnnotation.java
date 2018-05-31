package framework.core;

import java.lang.reflect.Method;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import framework.sdk.msg.Message;
import library.string.CharacterString;

@SuppressWarnings("serial")
public class ModuleAnnotation extends HttpServlet {
        private String className;

        public ModuleAnnotation() {
        }

        /**
         * 初始化<br />
         * 从参数读取配置文件信息
         */
        @Override
        public void init() throws ServletException {
                super.init();
                this.className = this.getInitParameter("className");
        }

        /**
         * doGet
         */
        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response) {
                doPost(request, response);
        }

        /**
         * doPost
         */
        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response) {
                try {
                        Document doc = DocumentHelper.createDocument();
                        doc.addDocType("xml", null, null);
                        Element classElement = doc.addElement("class");
                        Element descriptionElement = classElement.addElement("description");
                        Class<?> clazz = Class.forName(this.className);
                        // Class注解
                        Annotation[] classAnnotations = clazz.getAnnotations();
                        for (int i = 0; i < classAnnotations.length; i++) {
                                if (classAnnotations[i] instanceof framework.sdk.annotation.Class) {
                                        framework.sdk.annotation.Class c = (framework.sdk.annotation.Class) classAnnotations[i];
                                        if (null != c) {
                                                descriptionElement.setText(c.description());
                                        }
                                }
                        }
                        Element methodsElement = classElement.addElement("methods");
                        // Method注解
                        Method[] methods = clazz.getMethods();
                        for (int i = 0; i < methods.length; i++) {
                                framework.sdk.annotation.Method method = methods[i].getAnnotation(framework.sdk.annotation.Method.class);
                                if (null != method) {
                                        Element methodElement = methodsElement.addElement("method");
                                        Element nameElement = methodElement.addElement("name");
                                        nameElement.setText(methods[i].getName());
                                        Element methodDescElement = methodElement.addElement("description");
                                        methodDescElement.setText(method.description());
                                        Element parametersElement = methodElement.addElement("parameters");
                                        framework.sdk.annotation.Parameter[] parameters = method.parameters();
                                        for (int j = 0; j < parameters.length; j++) {
                                                Element parameterElement = parametersElement.addElement("parameter");
                                                framework.sdk.annotation.Parameter p = parameters[j];
                                                Element pNameElement = parameterElement.addElement("name");
                                                pNameElement.setText(p.name());
                                                Element pDescriptionElement = parameterElement.addElement("description");
                                                pDescriptionElement.setText(p.description());
                                                Element pTypeElement = parameterElement.addElement("type");
                                                pTypeElement.setText(p.type());
                                                Element pFormatElement = parameterElement.addElement("format");
                                                pFormatElement.setText(p.format());
                                        }
                                }
                        }
                        String s = doc.asXML();
                        if (null != s) {
                                Message.responseToClient(response, s);
                        } else {
                                Message.send(request, response, Message.STATUS.ERROR, Message.ERROR.OTHER, null, "Module Annotation Read Error: " + System.getProperty("line.separator") + this.className);
                        }
                } catch (Exception e) {
                        Message.send(request, response, Message.STATUS.EXCEPTION, Message.ERROR.OTHER, null, CharacterString.getExceptionStackTrace(e));
                }
        }
}