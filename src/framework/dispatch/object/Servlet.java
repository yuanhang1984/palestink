package framework.dispatch.object;

import framework.dispatch.object.Parameter;

public class Servlet {
        private String moduleName;
        private String name;
        private String description;
        private String namespace;
        private Parameter parameterList[];

        public Servlet(String moduleName, String name, String description, String namespace, Parameter[] parameterList) {
                this.moduleName = moduleName;
                this.name = name;
                this.description = description;
                this.namespace = namespace;
                this.parameterList = parameterList;
        }

        public String getModuleName() {
                return moduleName;
        }

        public String getName() {
                return name;
        }

        public String getDescription() {
                return description;
        }

        public String getNamespace() {
                return namespace;
        }

        public Parameter[] getParameterList() {
                return parameterList;
        }
}