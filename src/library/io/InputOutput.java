package library.io;

import java.awt.Image;
import java.awt.image.PixelGrabber;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 输入输出
 */
public class InputOutput {
        /**
         * 简单文件写入，如果文件不存在，先创建在写入；如果存在，则先删除原有文件再写入。
         * 
         * @param filePath 文件路径
         * @param data 待写入数据
         * @throws Exception
         */
        public static void simpleWriteFile(String filePath, byte[] data) throws Exception {
                FileOutputStream fos = null;
                try {
                        File f = new File(filePath);
                        if (f.exists()) {
                                f.delete();
                        } else {
                                f.createNewFile();
                        }
                        fos = new FileOutputStream(f);
                        fos.write(data);
                        fos.flush();
                } finally {
                        if (null != fos) {
                                fos.close();
                        }
                }
        }

        /**
         * 简单对象文件写入，如果文件不存在，先创建在写入；如果存在，则先删除原有文件再写入。
         * 
         * @param filePath 文件路径
         * @param obj 待写入对象（序列化）
         * @throws Exception
         */
        public static void simpleObjectWriteFile(String filePath, Object obj) throws Exception {
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;
                try {
                        File f = new File(filePath);
                        if (f.exists()) {
                                f.delete();
                        } else {
                                f.createNewFile();
                        }
                        fos = new FileOutputStream(f);
                        oos = new ObjectOutputStream(fos);
                        oos.writeObject(obj);
                        oos.flush();
                } finally {
                        if (null != oos) {
                                oos.close();
                        }
                        if (null != fos) {
                                fos.close();
                        }
                }
        }

        /**
         * 简单对象文件读取
         * 
         * @param filePath 文件路径
         * @return 读取到的对象，如果过为读取到对象，那么返回null。
         */
        public static Object simpleObjectReadFile(String filePath) throws Exception {
                FileInputStream fis = null;
                ObjectInputStream ois = null;
                try {
                        File f = new File(filePath);
                        if (!f.exists()) {
                                return null;
                        }
                        fis = new FileInputStream(f);
                        ois = new ObjectInputStream(fis);
                        return ois.readObject();
                } finally {
                        if (null != ois) {
                                ois.close();
                        }
                        if (null != fis) {
                                fis.close();
                        }
                }
        }

        /**
         * 简单StringBuilder文件读取
         * 
         * @param filePath
         * @return 文件内容的StringBuilder，如果没有读取到对象，那么返回null。
         */
        public static StringBuilder simpleStringBuilderReadFile(String filePath) throws Exception {
                StringBuilder sb = null;
                BufferedReader br = null;
                try {
                        File f = new File(filePath);
                        if (!f.exists()) {
                                return null;
                        }
                        br = new BufferedReader(new FileReader(f));
                        sb = new StringBuilder();
                        String s = "";
                        while ((s = br.readLine()) != null) {
                                sb.append(s);
                                sb.append(System.getProperty("line.separator"));
                        }
                        return sb;
                } finally {
                        if (null != br) {
                                br.close();
                        }
                }
        }

        /**
         * 规范路径<br />
         * java的配置路径中有两种方式“\\”和“/”，这里做统一的过滤，并且检查路径的最后是否以二者结尾，如果没有自动补充。<br />
         * 如此一来，可以方便的与文件名连接，组成完整的文件路径。<br />
         * 注意：如果path指向的路径不存在，那么只返回传入路径的替换结果。
         * 
         * @param path 待规范路径
         * @return 规范后的路径
         */
        public static String regulatePath(String path) {
                String s = path.replace("\\", "/");
                File f = new File(s);
                /*
                 * 注意：如果path指向的路径不存在，那么isDirectory为false
                 */
                if (f.isDirectory()) {
                        if ((s.length() - 1) != s.lastIndexOf("/")) {
                                s += "/";
                        }
                }
                return s;
        }

        /**
         * 向HttpServlet的客户端输出
         * 
         * @param response 待输出的response
         * @param msg 待输出的字符串数据
         * @throws Exception
         */
        public static void responseToClient(HttpServletResponse response, String msg) throws Exception {
                PrintWriter pw = null;
                try {
                        pw = response.getWriter();
                        pw.write(msg);
                        pw.flush();
                } finally {
                        if (null != pw) {
                                pw.close();
                        }
                }
        }

        /**
         * 清空目录<br />
         * 文件占用时，可能会因此锁定。因此，这个方法只供清理使用。<br />
         * 如若确保文件删除，请选用其他方法。
         * 
         * @param path 目录路径
         */
        public static void clearDir(File path) {
                if (path.isDirectory()) {
                        File[] files = path.listFiles();
                        for (int i = 0; i < files.length; i++) {
                                clearDir(files[i]);
                        }
                }
                path.delete();
        }

        /**
         * 重命名文件名
         * 
         * @param path 文件所在路径
         * @param oldFileName 原始文件名
         * @param newFileName 新文件名
         * @return 原始文件不存在，返回-1；新文件重名，返回-2；重命名失败，返回0；重命名成功，返回1。
         */
        public static int renameFile(String path, String oldFileName, String newFileName) {
                File oldFile = new File(InputOutput.regulatePath(path) + oldFileName);
                File newFile = new File(InputOutput.regulatePath(path) + newFileName);
                /**
                 * 原始文件不存在
                 */
                if (!oldFile.exists()) {
                        return -1;
                }
                /**
                 * 新文件有重名
                 */
                if (newFile.exists()) {
                        return -2;
                }
                if (oldFile.renameTo(newFile)) {
                        return 1;
                } else {
                        return 0;
                }
        }

        /**
         * 返回当前目录下（包括子目录）所有“文件夹”的路径集合
         * 
         * @param dirPath 目录路径
         * @return 如果目录路径不存在，返回null；如果目录路径不是目录，返回null；当前目录下所有文件路径的ArrayList。
         */
        public static ArrayList<String> getCurrentDirectoryFolderPath(String dirPath) {
                File dir = new File(InputOutput.regulatePath(dirPath));
                if (!dir.exists()) {
                        return null;
                }
                if (!dir.isDirectory()) {
                        return null;
                }
                File[] folders = dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathName) {
                                if (pathName.isDirectory()) {
                                        return true;
                                }
                                return false;
                        }
                });
                ArrayList<String> list = new ArrayList<String>();
                if (null != folders) {
                        for (int i = 0; i < folders.length; i++) {
                                list.add(InputOutput.regulatePath(folders[i].getAbsolutePath()));
                                ArrayList<String> subList = getCurrentDirectoryFolderPath(InputOutput.regulatePath(folders[i].getAbsolutePath()));
                                Iterator<String> iter = subList.iterator();
                                while (iter.hasNext()) {
                                        list.add(iter.next());
                                }
                        }
                }
                return list;
        }

        /**
         * 返回当前目录下（不包括子目录）所有“目录”的名称
         * 
         * @param dirPath 目录路径
         * @return 如果目录路径不存在，返回null；如果目录路径不是目录，返回null；当前目录下所有文件路径的ArrayList。
         */
        public static ArrayList<String> getCurrentDirectoryFolderName(String dirPath) {
                File dir = new File(InputOutput.regulatePath(dirPath));
                if (!dir.exists()) {
                        return null;
                }
                if (!dir.isDirectory()) {
                        return null;
                }
                File[] files = dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathName) {
                                /*
                                 * 只接受文件夹，不包括文件。
                                 */
                                if (pathName.isFile()) {
                                        return false;
                                }
                                return true;
                        }
                });
                if (null == files) {
                        return null;
                }
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < files.length; i++) {
                        list.add(files[i].getName());
                }
                return list;
        }

        /**
         * 返回当前目录下（不包括子目录）所有“文件”的名称集合
         * 
         * @param dirPath 目录路径
         * @param suffix 允许后缀。通过“,”可实现对多个后缀的限制，比如".html,.txt,.exe"一定要加“.”，如果没有后缀限制，可设置为null。
         * @return 如果目录路径不存在，返回null；如果目录路径不是目录，返回null；当前目录下所有文件路径的ArrayList。
         */
        public static ArrayList<String> getCurrentDirectoryFileName(String dirPath, final String suffix) {
                File dir = new File(InputOutput.regulatePath(dirPath));
                if (!dir.exists()) {
                        return null;
                }
                if (!dir.isDirectory()) {
                        return null;
                }
                File[] files = dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathName) {
                                /*
                                 * 只接受文件，不包括文件夹。
                                 */
                                if (pathName.isDirectory()) {
                                        return false;
                                }
                                /*
                                 * 筛选后缀
                                 */
                                if (null != suffix) {
                                        String[] arr = suffix.split(",");
                                        for (int i = 0; i < arr.length; i++) {
                                                if (pathName.getName().toLowerCase().endsWith(arr[i].toLowerCase()))
                                                        return true;
                                        }
                                        return false;
                                }
                                return true;
                        }
                });
                if (null == files) {
                        return null;
                }
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < files.length; i++) {
                        String s = files[i].getName();
                        list.add(s);
                }
                return list;
        }

        /**
         * 返回当前目录下（不包括子目录）所有“文件”的路径集合
         * 
         * @param dirPath 目录路径
         * @param suffix 允许后缀。通过“,”可实现对多个后缀的限制，比如".html,.txt,.exe"一定要加“.”，如果没有后缀限制，可设置为null。
         * @return 如果目录路径不存在，返回null；如果目录路径不是目录，返回null；当前目录下所有文件路径的ArrayList。
         */
        public static ArrayList<String> getCurrentDirectoryFilePath(String dirPath, final String suffix) {
                File dir = new File(InputOutput.regulatePath(dirPath));
                if (!dir.exists()) {
                        return null;
                }
                if (!dir.isDirectory()) {
                        return null;
                }
                File[] files = dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathName) {
                                /*
                                 * 只接受文件，不包括文件夹。
                                 */
                                if (pathName.isDirectory()) {
                                        return false;
                                }
                                /*
                                 * 筛选后缀
                                 */
                                if (null != suffix) {
                                        String[] arr = suffix.split(",");
                                        for (int i = 0; i < arr.length; i++) {
                                                if (pathName.getName().toLowerCase().endsWith(arr[i].toLowerCase()))
                                                        return true;
                                        }
                                        return false;
                                }
                                return true;
                        }
                });
                if (null == files) {
                        return null;
                }
                ArrayList<String> list = new ArrayList<String>();
                for (int i = 0; i < files.length; i++) {
                        String s = InputOutput.regulatePath(files[i].getAbsolutePath());
                        list.add(s);
                }
                return list;
        }

        /**
         * 获取当前目录下（包括子目录）所有“文件和文件夹”路径的集合<br />
         * 这个方法为getCurrentDirectoryFolderPath和getCurrentDirectoryFilePath的综合调用
         * 
         * @param dirPath 目录路径
         * @param suffix 允许后缀。通过“,”可实现对多个后缀的限制，比如".html,.txt,.exe"一定要加“.”，如果没有后缀限制，可设置为null。
         * @return 如果目录路径不存在，返回null；如果目录路径不是目录，返回null；当前目录下所有文件路径的ArrayList。
         */
        public static ArrayList<String> getCurrentDirectoryAllFilePath(String dirPath, String suffix) {
                ArrayList<String> list = new ArrayList<String>();
                // 添加dirPath根目录下的文件
                ArrayList<String> fileList = InputOutput.getCurrentDirectoryFilePath(dirPath, suffix);
                if (null == fileList) {
                        return null;
                }
                Iterator<String> fileIter = fileList.iterator();
                while (fileIter.hasNext()) {
                        String file = fileIter.next();
                        list.add(file);
                }
                ArrayList<String> dirList = InputOutput.getCurrentDirectoryFolderPath(dirPath);
                if (null == dirList) {
                        return null;
                }
                Iterator<String> dirIter = dirList.iterator();
                while (dirIter.hasNext()) {
                        String dir = dirIter.next();
                        list.add(dir);
                        // 添加dir子目录下的文件
                        fileList = InputOutput.getCurrentDirectoryFilePath(dir, suffix);
                        if (null == fileList) {
                                return null;
                        }
                        fileIter = fileList.iterator();
                        while (fileIter.hasNext()) {
                                String file = fileIter.next();
                                list.add(file);
                        }
                }
                return list;
        }

        /**
         * 创建原有图片的灰色内存图片（通常用于只读显示）<br />
         * 由于这个方法创建的是内存图片，所以需要使用方用Image.create()接收这个方法创建的MemoryImageSource<br />
         * 灰度变换的算法其实很简单，只要提取每个象素点的红、绿、蓝三原色，然后根据公式：灰度值= 红色亮度值*30%+绿色亮 度值*59%+蓝色亮度值*11%，计算出一个灰度值，并将其作为 红，绿，蓝三原色的新值重新写回显存即可。
         * 
         * @param original 原有图片Image
         * @param width 原有图片的宽度
         * @param height 原有图片的高度
         * @return 创建成功，返回灰色图片的MemoryImageSource；创建失败，返回null。
         */
        public static MemoryImageSource createGrayMemoryImageSource(Image original, int width, int height) throws Exception {
                int[] buf = new int[width * height];
                PixelGrabber pg = new PixelGrabber(original, 0, 0, width, height, buf, 0, width);
                if (!pg.grabPixels()) {
                        return null;
                }
                for (int i = 0; i < height; i++) {
                        for (int j = 0; j < width; j++) {
                                int color = buf[width * i + j];
                                int alpha = (color & 0xFF000000) >> 24;
                                int red = (int) (((color & 0x00FF0000) >> 16) * 0.3);
                                int green = (int) (((color & 0x0000FF00) >> 8) * 0.59);
                                int blue = (int) ((color & 0x000000FF) * 0.11);
                                color = red + green + blue;
                                color = (alpha << 24) | (color << 16) | (color << 8) | color;
                                buf[width * i + j] = color;
                        }
                }
                return new MemoryImageSource(width, height, buf, 0, width);
        }

        /**
         * 当前路径转为json格式
         * 
         * @param dirPath 路径
         * @return { "name": "c:", "type": "directory", "sub": [] } 或{ "name": "c:", "type": "file" }
         */
        public static JSONObject changePathToJSONObject(String dirPath) {
                JSONObject obj = new JSONObject();
                File f = new File(dirPath);
                obj.put("name", f.getName());
                if (f.isDirectory()) {
                        obj.put("type", "directory");
                        obj.put("sub", new JSONArray());
                } else {
                        obj.put("type", "file");
                }
                return obj;
        }

        /**
         * 路径下目录（包括子目录）转为json格式
         * 
         * @param dirPath 路径
         * @param parent 返回的JSONArray格式，如：[ { "sub": [ { "sub": [ { "name": "a1.txt", "type": "file" }, { "name": "a2.txt", "type": "file" }, { "name": "a3.txt", "type": "file" }, { "sub": [ { "name": "aa1.txt", "type": "file" }, { "name": "aa2.txt", "type": "file" } ], "name": "aa", "type": "directory" }, { "sub": [ { "name": "bb1.txt", "type": "file" }, { "name": "bb2.txt", "type": "file" }, { "name": "bb3.txt", "type": "file" }, { "name": "bb4.txt", "type": "file" } ], "name": "bb", "type": "directory" } ], "name": "a", "type": "directory" }, { "sub": [ { "name": "b1.txt", "type": "file" }, { "sub": [ { "name": "bb1.txt", "type": "file" }, { "sub": [ { "name": "bbb1.txt", "type": "file" } ], "name": "bbb", "type": "directory" } ], "name": "bb", "type": "directory" } ], "name": "b", "type": "directory" }, { "sub": [], "name": "c", "type": "directory" }, { "name": "root1.txt", "type": "file" }, { "name": "root2.txt", "type": "file" } ], "name": "test", "type": "directory" } ]
         */
        public static void changePathToJSONArray(String dirPath, JSONArray parent) {
                File dir = new File(InputOutput.regulatePath(dirPath));
                File[] folders = dir.listFiles();
                /*
                 * 存入当前目录
                 */
                JSONObject obj = changePathToJSONObject(dirPath);
                if (null != folders) {
                        for (int i = 0; i < folders.length; i++) {
                                JSONArray a = obj.getJSONArray("sub");
                                if (null != a) {
                                        /*
                                         * 文件夹
                                         */
                                        changePathToJSONArray(folders[i].getAbsolutePath(), a);
                                }
                        }
                }
                parent.put(obj);
        }

        /**
         * 获得某一目录下文件的绝对路径
         * 
         * @param parentDir 目录的绝对路径
         * @param fileName 目录下文件的绝对路径
         * @return 返回目录下文件的绝对路径。比如：dir为c:/，fileName为c:/123/456/1.txt，那么返回123/456/1.txt。
         */
        public static String getAbsolutePathUnderDirectory(String parentDir, String fileName) {
                File dir = new File(parentDir);
                File file = new File(fileName);
                String s = file.getName();
                for (;;) {
                        file = file.getParentFile();
                        if (null == file) {
                                break;
                        }
                        if (file.equals(dir)) {
                                break;
                        } else {
                                s = file.getName() + "/" + s;
                        }
                }
                return s;
        }

        /**
         * 压缩目录成jar
         * 
         * @param dir 待压缩的目录
         * @param jarFilePath jar文件的输出路径
         * @throws Exception
         */
        public static void compressDirectoryToJarFile(String dir, String jarFilePath) throws Exception {
                File directory = new File(dir);
                if (!directory.exists()) {
                        throw new FileNotFoundException("Directory " + dir + "Was Not Found");
                }
                File file = new File(jarFilePath);
                if (!file.getParentFile().exists()) {
                        file.getParentFile().mkdirs();
                }
                ArrayList<File> fileList = new ArrayList<File>();
                ArrayList<String> allFilePathList = InputOutput.getCurrentDirectoryAllFilePath(dir, null);
                Iterator<String> allFilePathIter = allFilePathList.iterator();
                while (allFilePathIter.hasNext()) {
                        File f = new File(allFilePathIter.next());
                        if (f.isFile()) {
                                fileList.add(f);
                        }
                }
                FileOutputStream fos = null;
                JarOutputStream jos = null;
                FileInputStream fis = null;
                fos = new FileOutputStream(jarFilePath);
                jos = new JarOutputStream(fos);
                JarEntry je = null;
                Iterator<File> iter = fileList.iterator();
                try {
                        while (iter.hasNext()) {
                                File f = iter.next();
                                try {
                                        fis = new FileInputStream(f);
                                        je = new JarEntry(InputOutput.getAbsolutePathUnderDirectory(dir, f.getAbsolutePath()));
                                        je.setSize(f.length());
                                        je.setTime(f.lastModified());
                                        jos.putNextEntry(je);
                                        byte[] buffer = new byte[10240];
                                        int nBytes = 0;
                                        while (0 < (nBytes = fis.read(buffer))) {
                                                jos.write(buffer, 0, nBytes);
                                        }
                                        jos.flush();
                                } finally {
                                        if (null != fis) {
                                                fis.close();
                                        }
                                }
                        }
                } finally {
                        if (null != jos) {
                                jos.close();
                        }
                        if (null != fos) {
                                fos.flush();
                                fos.close();
                        }
                }
        }

        /**
         * 解压jar文件至指定目录，以jar文件名为子目录。
         * 
         * @param jarFilePath jar文件的路径
         * @param outputPath 解压目录
         * @throws Exception
         */
        public static void decompressDirectoryToJarFile(String jarFilePath, String outputPath) throws Exception {
                JarFile jf = null;
                InputStream is = null;
                FileOutputStream fos = null;
                jf = new JarFile(jarFilePath);
                try {
                        for (Enumeration<JarEntry> e = jf.entries(); e.hasMoreElements();) {
                                JarEntry je = (JarEntry) e.nextElement();
                                String outputFileName = outputPath + je.getName();
                                File f = new File(outputFileName);
                                if (je.isDirectory()) {
                                        if (!f.exists()) {
                                                f.mkdirs();
                                        }
                                } else {
                                        File pf = f.getParentFile();
                                        if (!pf.exists()) {
                                                pf.mkdirs();
                                        }
                                        try {
                                                is = jf.getInputStream(je);
                                                fos = new FileOutputStream(f);
                                                byte[] buffer = new byte[10240];
                                                int nBytes = 0;
                                                while (0 < (nBytes = is.read(buffer))) {
                                                        fos.write(buffer, 0, nBytes);
                                                }
                                                fos.flush();
                                        } finally {
                                                if (null != fos) {
                                                        fos.close();
                                                }
                                                if (null != is) {
                                                        is.close();
                                                }
                                        }
                                }
                        }
                } finally {
                        if (null != jf) {
                                jf.close();
                        }
                }
        }

        /**
         * 压缩数据成zip
         * 
         * @param is 待压缩的数据
         * @param fileName 待压缩的数据在zip文件中的文件名
         * @param zipFilePath zip文件的输出路径
         * @throws Exception
         */
        public static void compressDataToZipFile(InputStream is, String fileName, String zipFilePath) throws Exception {
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                ZipOutputStream zos = null;
                BufferedOutputStream bos = null;
                ZipEntry ze = null;
                File zipFile = null;
                try {
                        zipFile = new File(zipFilePath);
                        bis = new BufferedInputStream(is);
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(fos);
                        ze = new ZipEntry(fileName);
                        zos.putNextEntry(ze);
                        bos = new BufferedOutputStream(zos);
                        byte[] buffer = new byte[10240];
                        int nBytes = 0;
                        while (0 < (nBytes = bis.read(buffer))) {
                                bos.write(buffer, 0, nBytes);
                        }
                        bos.flush();
                } finally {
                        if (null != bos) {
                                bos.close();
                        }
                        if (null != zos) {
                                zos.close();
                        }
                        if (null != fos) {
                                fos.close();
                        }
                        if (null != bis) {
                                bis.close();
                        }
                }
        }

        /**
         * 压缩文件成zip
         * 
         * @param filePath 待压缩文件路径
         * @param zipFilePath zip文件的输出路径
         * @throws Exception
         */
        public static void compressFileToZipFile(String filePath, String zipFilePath) throws Exception {
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                FileOutputStream fos = null;
                ZipOutputStream zos = null;
                BufferedOutputStream bos = null;
                ZipEntry ze = null;
                File file = null;
                File zipFile = null;
                try {
                        file = new File(filePath);
                        zipFile = new File(zipFilePath);
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        fos = new FileOutputStream(zipFile);
                        zos = new ZipOutputStream(fos);
                        ze = new ZipEntry(file.getName());
                        ze.setSize(file.length());
                        ze.setTime(file.lastModified());
                        zos.putNextEntry(ze);
                        bos = new BufferedOutputStream(zos);
                        byte[] buffer = new byte[10240];
                        int nBytes = 0;
                        while (0 < (nBytes = bis.read(buffer))) {
                                bos.write(buffer, 0, nBytes);
                        }
                        bos.flush();
                } finally {
                        if (null != bos) {
                                bos.close();
                        }
                        if (null != zos) {
                                zos.close();
                        }
                        if (null != fos) {
                                fos.close();
                        }
                        if (null != bis) {
                                bis.close();
                        }
                        if (null != fis) {
                                fis.close();
                        }
                }
        }

        /**
         * 复制文件至目录（如若重复，则删除再写入）
         * 
         * @param srcFilePath 源文件路径
         * @param destFilePath 目标文件路径
         * @return
         * @throws Exception
         */
        public static void copyFile(String srcFilePath, String destFilePath) throws Exception {
                File srcFile = new File(srcFilePath);
                if (!srcFile.exists()) {
                        throw new FileNotFoundException("Source File " + srcFilePath + "Was Not Found");
                } else if (!srcFile.isFile()) {
                        throw new FileNotFoundException("Source File " + srcFilePath + "Was Not File");
                }
                File destFile = new File(destFilePath);
                if (destFile.exists()) {
                        new File(destFilePath).delete();
                } else {
                        if (!destFile.getParentFile().exists()) {
                                destFile.getParentFile().mkdirs();
                        }
                }
                int nBytes = 0;
                InputStream is = null;
                OutputStream os = null;
                try {
                        is = new FileInputStream(srcFile);
                        os = new FileOutputStream(destFile);
                        byte[] buffer = new byte[10240];
                        while (0 < (nBytes = is.read(buffer))) {
                                os.write(buffer, 0, nBytes);
                        }
                        os.flush();
                } finally {
                        if (null != os) {
                                os.close();
                        }
                        if (null != is) {
                                is.close();
                        }
                }
        }

        /**
         * 复制文件夹
         * 
         * @param srcDirPath 源文件夹路径
         * @param destDirPath 目标文件夹路径
         * @throws Exception
         */
        public static void copyDirectory(String srcDirPath, String destDirPath) throws Exception {
                File destDirFile = new File(destDirPath);
                if (!destDirFile.exists()) {
                        destDirFile.mkdirs();
                }
                File directory = new File(srcDirPath);
                if (!directory.exists()) {
                        throw new FileNotFoundException("Directory " + srcDirPath + "Was Not Found");
                }
                if (!directory.isDirectory()) {
                        throw new FileNotFoundException("Directory " + srcDirPath + "Was Not Directory");
                }
                ArrayList<String> list = InputOutput.getCurrentDirectoryAllFilePath(srcDirPath, null);
                Iterator<String> iter = list.iterator();
                while (iter.hasNext()) {
                        String fileName = iter.next();
                        File f = new File(fileName);
                        String subFileName = InputOutput.getAbsolutePathUnderDirectory(srcDirPath, fileName);
                        String destFilePath = InputOutput.regulatePath(destDirPath);
                        destFilePath += subFileName;
                        if (f.isFile()) {
                                InputOutput.copyFile(f.getAbsolutePath(), InputOutput.regulatePath(destFilePath));
                        } else {
                                File dir = new File(InputOutput.regulatePath(destFilePath));
                                dir.mkdirs();
                        }
                }
        }
}