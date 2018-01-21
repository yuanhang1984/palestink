package framework.dispatch.object;

public class Parameter {
        private String name;
        private String description;
        private String type;
        private String format;
        private String transform;
        private Object constant;
        private boolean allowNull;
        private long fileMaxSize;

        public Parameter(String name, String description, String type, String format, String transform, Object constant, boolean allowNull, long fileMaxSize) {
                this.name = name;
                this.description = description;
                this.type = type;
                this.format = format;
                this.transform = transform;
                this.constant = constant;
                this.allowNull = allowNull;
                this.fileMaxSize = fileMaxSize;
        }

        public String getName() {
                return name;
        }

        public String getDescription() {
                return description;
        }

        public String getType() {
                return type;
        }

        public String getFormat() {
                return format;
        }

        public String getTransform() {
                return transform;
        }

        public Object getConstant() {
                return constant;
        }

        public boolean getAllowNull() {
                return allowNull;
        }

        public long getFileMaxSize() {
                return fileMaxSize;
        }
}