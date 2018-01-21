package module.file_storage.necessary;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.HashMap;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import framework.sdk.Framework;
import framework.sdk.msg.Message;
import framework.sdk.spec.module.FileStorage;
import framework.sdk.spec.module.necessary.CustomAction;
import framework.ext.factory.DbFactory;
import framework.sdbo.object.SqlRepository;
import library.database.DatabaseKit;
import library.string.CharacterString;
import org.dom4j.Element;
import org.apache.commons.fileupload.FileItem;

public class Custom extends CustomAction implements FileStorage {
        public static final String MODULE_NAME = "file_storage.Custom";
        private HttpServletResponse httpServletResponse;
        private HashMap<String, Object> parameter;
        private Connection connection;

        public Custom(HttpServlet httpServlet, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Connection connection, HashMap<String, Object> parameter) {
                super(httpServlet, httpServletRequest, httpServletResponse, connection, parameter);
                this.httpServletResponse = httpServletResponse;
                this.parameter = parameter;
                this.connection = connection;
        }

        /**
         * 上传临时文件
         * 
         * [参数列表所需参数]
         * uuid: 文件的uuid（这里要从前台接收而不是后台生成，原因是这样做可以通过前台继续调用uuid获取当前文件的数据）
         * attachment: 上传的文件
         */
        @Override
        public Message uploadTemporaryFile() {
                Message msg = new Message();
                FileItem attachment = null;
                HashMap<String, Object> p = null;
                InputStream is = null;
                String sql = null;
                PreparedStatement ps = null;
                int res = 0;
                Element sqlRoot = SqlRepository.get("file_storage");
                if (null == sqlRoot) {
                        msg.setResult(Message.RESULT.NO_MODULE_SQL);
                        msg.setDetail("file_storage");
                        return msg;
                }
                try {
                        // 根据当前时间和临时文件的有效期设置临时文件的过期时间
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MINUTE, Config.TEMPORARY_FILE_LIFE_CYCLE);
                        attachment = (FileItem) parameter.get("attachment");
                        if (null == attachment) {
                                msg.setResult(Message.RESULT.PARAMETER_INVALID);
                                msg.setDetail("attachment");
                                return msg;
                        }
                        is = attachment.getInputStream();
                        // 获取文件前缀
                        String fileName = attachment.getName().substring(0, attachment.getName().indexOf("."));
                        if (fileName.length() >= 128) {
                                // 如果文件名长度大于等于128（数据库列数的设置），则自动截取。
                                fileName = fileName.substring(0, 128);
                        }
                        // 获取文件后缀
                        String fileSuffix = attachment.getName().substring(attachment.getName().lastIndexOf(".") + 1);
                        if (fileSuffix.length() >= 16) {
                                // 如果文件后缀长度大于等于16（数据库列数的设置），则自动截取。
                                fileSuffix = fileSuffix.substring(0, 16);
                        }
                        // 将文件内容写入“文件仓库”表（SimpleDBO中只是简单的sql操作，不包括二进制文件的添加。所以这里需要用传统语句的方式实现功能）
                        sql = DatabaseKit.composeSql(sqlRoot, "insertStorageRepository", null);
                        if (0 >= sql.trim().length()) {
                                msg.setResult(Message.RESULT.COMPOSE_SQL_ERROR);
                                msg.setDetail("insertStorageRepository");
                                return msg;
                        }
                        ps = this.connection.prepareStatement(sql);
                        ps.setString(1, CharacterString.getUuidStr(true));
                        ps.setString(2, (String) parameter.get("uuid"));
                        ps.setBinaryStream(3, is);
                        res = ps.executeUpdate();
                        if (0 >= res) {
                                msg.setResult(Message.RESULT.IDU_NO_DATA);
                                msg.setDetail("insertStorageRepository");
                                return msg;
                        }
                        p = new HashMap<String, Object>();
                        p.put("uuid", parameter.get("uuid"));
                        p.put("name", fileName);
                        p.put("suffix", fileSuffix);
                        p.put("expire_datetime", new java.sql.Timestamp(cal.getTimeInMillis()));
                        sql = DatabaseKit.composeSql(sqlRoot, "insertStorageFile", p);
                        if (0 >= sql.trim().length()) {
                                msg.setResult(Message.RESULT.COMPOSE_SQL_ERROR);
                                msg.setDetail("insertStorageFile");
                                return msg;
                        }
                        res = DbFactory.iduExecute(this.connection, sql);
                        if (0 >= res) {
                                msg.setResult(Message.RESULT.IDU_NO_DATA);
                                msg.setDetail("insertStorageFile");
                                return msg;
                        }
                        msg.setResult(Message.RESULT.SUCCESS);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Custom.MODULE_NAME, e.toString());
                        msg.setResult(Message.RESULT.EXCEPTION);
                        msg.setDetail(e.toString());
                        return msg;
                } finally {
                        try {
                                if (null != is) {
                                        is.close();
                                }
                                if (null != ps) {
                                        ps.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.warn(Custom.MODULE_NAME, e.toString());
                                msg.setResult(Message.RESULT.EXCEPTION);
                                msg.setDetail(e.toString());
                                return msg;
                        }
                }
        }

        /**
         * 获取文件
         * 
         * [参数列表所需参数]
         * uuid: 文件的uuid
         * download: 是否下载文件 0 不下载 1 下载
         */
        @Override
        public Message getFile() {
                Message msg = new Message();
                HashMap<String, Object> p = null;
                InputStream is = null;
                String sql = null;
                PreparedStatement ps = null;
                ResultSet rs = null;
                OutputStream os = null;
                Element sqlRoot = SqlRepository.get("file_storage");
                if (null == sqlRoot) {
                        msg.setResult(Message.RESULT.NO_MODULE_SQL);
                        msg.setDetail("file_storage");
                        return msg;
                }
                try {
                        p = new HashMap<String, Object>();
                        p.put("uuid", parameter.get("uuid"));
                        sql = DatabaseKit.composeSql(sqlRoot, "selectStorageFileRepository", p);
                        if (0 >= sql.trim().length()) {
                                msg.setResult(Message.RESULT.COMPOSE_SQL_ERROR);
                                msg.setDetail("selectStorageFileRepository");
                                return msg;
                        }
                        ps = this.connection.prepareStatement(sql);
                        rs = ps.executeQuery();
                        if (rs.next()) {
                                String fileName = rs.getString("name");
                                String fileSuffix = rs.getString("suffix");
                                is = rs.getBinaryStream("data");
                                int size = 0;
                                byte[] buf = new byte[10240];
                                Integer download = (Integer) parameter.get("download");
                                if (1 == download) {
                                        this.httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName + "." + fileSuffix);
                                } else {
                                        this.httpServletResponse.setContentType(Files.probeContentType(Paths.get(fileName + "." + fileSuffix)));
                                }
                                os = this.httpServletResponse.getOutputStream();
                                while (-1 != (size = is.read(buf))) {
                                        try {
                                                os.write(buf, 0, size);
                                        } catch (Exception e) {
                                                msg.setResult(Message.RESULT.EXCEPTION);
                                                msg.setDetail(e.toString());
                                                return msg;
                                        }
                                }
                        } else {
                                msg.setResult(Message.RESULT.FILE_NOT_EXIST);
                                msg.setDetail(parameter.get("uuid"));
                                return msg;
                        }
                        msg.setResult(Message.RESULT.ALREADY_FEEDBACK_TO_CLIENT);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Custom.MODULE_NAME, e.toString());
                        msg.setResult(Message.RESULT.EXCEPTION);
                        msg.setDetail(e.toString());
                        return msg;
                } finally {
                        try {
                                if (null != rs) {
                                        rs.close();
                                }
                                if (null != is) {
                                        is.close();
                                }
                                if (null != os) {
                                        os.close();
                                }
                                if (null != ps) {
                                        ps.close();
                                }
                        } catch (Exception e) {
                                Framework.LOG.warn(Custom.MODULE_NAME, e.toString());
                                msg.setResult(Message.RESULT.EXCEPTION);
                                msg.setDetail(e.toString());
                                return msg;
                        }
                }
        }

        @Override
        public Message savePermanentFile() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message savePermanentFiles() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message removeFile() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message removeFiles() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message checkFileExist() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message checkFilesExist() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message checkFileNotExist() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message checkFilesNotExist() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message modifyAttachment() {
                // TODO Auto-generated method stub
                return null;
        }

        @Override
        public Message removeAttachment() {
                // TODO Auto-generated method stub
                return null;
        }
}