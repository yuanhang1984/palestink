package framework.dispatch.object;

import framework.dispatch.object.Parameter;

public class Servlet {
        private String name;
        private String description;
        private String namespace;
        private String permission;
        private Parameter parameterList[];

        public Servlet(String name, String description, String namespace, String permission, Parameter[] parameterList) {
                this.name = name;
                this.description = description;
                this.namespace = namespace;
                this.permission = permission;
                this.parameterList = parameterList;
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

        public String getPermission() {
                return permission;
        }

        public Parameter[] getParameterList() {
                return parameterList;
        }
}