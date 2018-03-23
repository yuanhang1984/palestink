package framework.sdbo.object;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Namespace {
        public static final String NO_PARAMETER = "no_parameter";

        /*
         * 类型
         */
        private String type;

        /*
         * 模块名称
         */
        private String moduleName;

        /*
         * id
         */
        private String id;

        /*
         * 参数列表
         */
        private String[] parameterList;

        public Namespace(String type, String moduleName, String id, String[] parameterList) {
                this.type = type;
                this.moduleName = moduleName;
                this.id = id;
                this.parameterList = parameterList;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        public String getModuleName() {
                return moduleName;
        }

        public void setModuleName(String moduleName) {
                this.moduleName = moduleName;
        }

        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String[] getParameterList() {
                return parameterList;
        }

        public void setParameterList(String[] parameterList) {
                this.parameterList = parameterList;
        }

        /**
         * 解析namespace
         * @param namespace namespace
         * @return 返回ArrayList<Namespace>
         */
        public static ArrayList<Namespace> analyseNamespace(String namespace) {
                ArrayList<Namespace> list = new ArrayList<Namespace>();
                String arr[] = namespace.split(";");
                for (int i = 0; i < arr.length; i++) {
                        String str = arr[i];
                        Pattern pattern = Pattern.compile("\\[(.+?)\\]\\[(.+?)\\]\\[(.+?)\\]\\[(.*?)\\]");
                        Matcher matcher = pattern.matcher(str);
                        if (matcher.find()) {
                                String type = matcher.group(1).trim();
                                String moduleName = matcher.group(2).trim();
                                String id = matcher.group(3).trim();
                                String parameter = matcher.group(4);
                                if ((null == parameter) || (0 >= parameter.length())) {
                                        parameter = Namespace.NO_PARAMETER;
                                }
                                if ((0 >= type.length()) || (0 >= moduleName.length()) || (0 >= id.length()) || (0 >= parameter.length())) {
                                        break;
                                }
                                list.add(new Namespace(type, moduleName, id, parameter.split(",")));
                        }
                }
                return list;
        }
}