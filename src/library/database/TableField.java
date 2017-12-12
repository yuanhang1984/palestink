package library.database;

public class TableField {
        public static final String allow_null_true = "Null";
        public static final String allow_null_false = "Not Null";

        private String fieldName;
        private String fieldType;
        private int fieldSize;
        private String allowNull;

        /**
         * 构造函数
         * 
         * @param fieldName 字段名称
         * @param fieldType 字段类型
         * @param fieldSize 字段容量
         * @param allowNull 是否允许为空，Null或Not Null
         */
        public TableField(String fieldName, String fieldType, int fieldSize, String allowNull) {
                super();
                this.fieldName = fieldName;
                this.fieldType = fieldType;
                this.fieldSize = fieldSize;
                this.allowNull = allowNull;
        }

        public String getFieldName() {
                return fieldName;
        }

        public void setFieldName(String fieldName) {
                this.fieldName = fieldName;
        }

        public String getFieldType() {
                return fieldType;
        }

        public void setFieldType(String fieldType) {
                this.fieldType = fieldType;
        }

        public int getFieldSize() {
                return fieldSize;
        }

        public void setFieldSize(int fieldSize) {
                this.fieldSize = fieldSize;
        }

        public String getAllowNull() {
                return allowNull;
        }

        public void setAllowNull(String allowNull) {
                this.allowNull = allowNull;
        }
}