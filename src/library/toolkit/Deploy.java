package library.toolkit;

import java.io.File;
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
                InputOutput.copyDirectory(projectRootPath + "build/classes/framework/sdk", tmpDirPath + "palestinkSdk/framework/sdk");
                InputOutput.compressDirectoryToJarFile(tmpDirPath + "palestinkSdk", tmpDirPath + "palestinkSdk/jar/palestinkSdk.jar");
                InputOutput.copyFile(tmpDirPath + "palestinkSdk/jar/palestinkSdk.jar", projectRootPath + "WebContent/WEB-INF/lib/palestinkSdk.jar");
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

        public static void main(String[] args) {
                new Deploy();
        }
}