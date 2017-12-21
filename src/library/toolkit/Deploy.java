package library.toolkit;

import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import library.io.InputOutput;
import library.string.CharacterString;

public class Deploy {
        /*
         * 临时目录路径
         */
        private String tmpDirPath;

        /*
         * 项目根路径
         */
        private String projectRootPath;

        public Deploy() {
                try {
                        // 初始化临时目录路径
                        tmpDirPath = InputOutput.regulatePath("E:/tmp/deploy/" + CharacterString.getCurrentFormatDateTime("yyyyMMddHHmmssSSS") + "/");
                        // 创建临时目录
                        File tmpDir = new File(tmpDirPath);
                        tmpDir.mkdirs();
                        // 初始化项目根路径
                        projectRootPath = InputOutput.regulatePath(new File(Deploy.class.getResource("").getPath()).getParentFile().getParentFile().getParentFile().getParentFile().getAbsolutePath());
                        // 发布框架
                        this.deployPalestink();
                        // 发布框架Sdk
                        this.deployPalestinkSdk();
                        // 发布Log源码
                        this.deployLogSource();
                        // 发布Db源码
                        this.deployDbSource();
                        // 发布模块源码
                        this.deployModuleSource();
                        // 清理临时目录
                        InputOutput.clearDir(tmpDir);
                } catch (Exception e) {
                        System.out.println(e.toString());
                }
                System.out.println("Depoly Complete!");
        }

        /**
         * 发布框架
         */
        private void deployPalestink() throws Exception {
                InputOutput.copyDirectory(projectRootPath + "build/classes", tmpDirPath + "palestink");
                InputOutput.compressDirectoryToJarFile(tmpDirPath + "palestink", tmpDirPath + "palestink/jar/palestink.jar");
                InputOutput.copyFile(tmpDirPath + "palestink/jar/palestink.jar", projectRootPath + "WebContent/WEB-INF/lib/palestink.jar");
        }

        /**
         * 发布框架Sdk
         */
        private void deployPalestinkSdk() throws Exception {
                InputOutput.copyDirectory(projectRootPath + "build/classes/framework/sdk", tmpDirPath + "palestink_sdk/framework/sdk");
                InputOutput.copyDirectory(projectRootPath + "build/classes/library", tmpDirPath + "palestink_sdk/library");
                InputOutput.compressDirectoryToJarFile(tmpDirPath + "palestink_sdk", tmpDirPath + "palestink_sdk/jar/palestink_sdk.jar");
                InputOutput.copyFile(tmpDirPath + "palestink_sdk/jar/palestink_sdk.jar", projectRootPath + "WebContent/WEB-INF/lib/palestink_sdk.jar");
        }

        /**
         * 发布Log源码
         */
        private void deployLogSource() throws Exception {
                InputOutput.copyDirectory(projectRootPath + "src/ext/log", projectRootPath + "WebContent/WEB-INF/ext/log/src");
        }

        /**
         * 发布Db源码
         */
        private void deployDbSource() throws Exception {
                InputOutput.copyDirectory(projectRootPath + "src/ext/db", projectRootPath + "WebContent/WEB-INF/ext/db/src");
        }

        /**
         * 发布模块源码（module目录下所有模块）
         */
        private void deployModuleSource() throws Exception {
                ArrayList<String> moduleList = InputOutput.getCurrentDirectoryFolderName(projectRootPath + "src/module");
                if (null != moduleList) {
                        Iterator<String> moduleIter = moduleList.iterator();
                        while (moduleIter.hasNext()) {
                                String moduleName = moduleIter.next();
                                InputOutput.clearDir(new File(projectRootPath + "WebContent/WEB-INF/module/" + moduleName + "/src"));
                                InputOutput.copyDirectory(projectRootPath + "src/module/" + moduleName, projectRootPath + "WebContent/WEB-INF/module/" + moduleName + "/src/" + moduleName);
                        }
                }
        }

        public static void main(String[] args) {
                new Deploy();
        }
}