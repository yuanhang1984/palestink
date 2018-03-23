package module.file_storage.necessary;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Calendar;
import java.util.ArrayList;
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
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail("file_storage");
                        return msg;
                }
                try {
                        // 根据当前时间和临时文件的有效期设置临时文件的过期时间
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.MINUTE, Config.TEMPORARY_FILE_LIFE_CYCLE);
                        attachment = (FileItem) parameter.get("attachment");
                        if (null == attachment) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.PARAMETER_FORMAT_ERROR);
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
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail("insertStorageRepository");
                                return msg;
                        }
                        ps = this.connection.prepareStatement(sql);
                        ps.setString(1, CharacterString.getUuidStr(true));
                        ps.setString(2, (String) parameter.get("uuid"));
                        ps.setBinaryStream(3, is);
                        res = ps.executeUpdate();
                        if (0 >= res) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.IDU_NO_DATA);
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
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail("insertStorageFile");
                                return msg;
                        }
                        res = DbFactory.iduExecute(this.connection, sql);
                        if (0 >= res) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.IDU_NO_DATA);
                                msg.setDetail("insertStorageFile");
                                return msg;
                        }
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
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
                                Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
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
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail("file_storage");
                        return msg;
                }
                try {
                        p = new HashMap<String, Object>();
                        p.put("uuid", parameter.get("uuid"));
                        sql = DatabaseKit.composeSql(sqlRoot, "selectStorageFileRepository", p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
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
                                                msg.setStatus(Message.STATUS.EXCEPTION);
                                                msg.setError(Message.ERROR.OTHER);
                                                msg.setDetail(e.toString());
                                                return msg;
                                        }
                                }
                        } else {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("File Not Exist [" + parameter.get("uuid") + "]");
                                return msg;
                        }
                        msg.setSign(Message.SIGN.ALREADY_FEEDBACK_TO_CLIENT);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
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
                                Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                                msg.setStatus(Message.STATUS.EXCEPTION);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail(e.toString());
                                return msg;
                        }
                }
        }

        /**
         * 保存为正式文件（内部调用）
         * 
         * @param uuid 文件的uuid
         * @return Message对象
         */
        private Message inline_savePermanentFile(String uuid) {
                Message msg = new Message();
                HashMap<String, Object> p = null;
                int res = 0;
                String sql = null;
                Element sqlRoot = SqlRepository.get("file_storage");
                if (null == sqlRoot) {
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail("file_storage");
                        return msg;
                }
                try {
                        // 根据当前时间和正式文件的有效期设置正式文件的过期时间
                        Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.YEAR, Config.PERMANENT_FILE_LIFE_CYCLE);
                        p = new HashMap<String, Object>();
                        p.put("uuid", uuid);
                        p.put("expire_datetime", new java.sql.Timestamp(cal.getTimeInMillis()));
                        sql = DatabaseKit.composeSql(sqlRoot, "updateStorageFile", p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail("updateStorageFile");
                                return msg;
                        }
                        res = DbFactory.iduExecute(this.connection, sql);
                        if (0 >= res) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.OTHER);
                                msg.setDetail("updateStorageFile");
                                return msg;
                        }
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * 保存为正式文件
         * 
         * [参数列表所需参数]
         * uuid: 文件的uuid
         */
        @Override
        public Message savePermanentFile() {
                return this.inline_savePermanentFile((String) parameter.get("uuid"));
        }

        /**
         * 删除文件（内部调用）
         * 
         * @param uuid 文件的uuid
         * @return Message对象
         */
        private Message inline_removeFile(String uuid) {
                Message msg = new Message();
                HashMap<String, Object> p = null;
                int res = 0;
                String sql = null;
                Element sqlRoot = SqlRepository.get("file_storage");
                if (null == sqlRoot) {
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail("file_storage");
                        return msg;
                }
                try {
                        // 根据当前时间和正式文件的有效期设置正式文件的过期时间
                        p = new HashMap<String, Object>();
                        p.put("uuid", uuid);
                        sql = DatabaseKit.composeSql(sqlRoot, "deleteStorageFile", p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail("deleteStorageFile");
                                return msg;
                        }
                        res = DbFactory.iduExecute(this.connection, sql);
                        if (0 >= res) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.IDU_NO_DATA);
                                msg.setDetail("deleteStorageFile");
                                return msg;
                        }
                        sql = DatabaseKit.composeSql(sqlRoot, "deleteStorageRepository", p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail("deleteStorageRepository");
                                return msg;
                        }
                        res = DbFactory.iduExecute(this.connection, sql);
                        if (0 >= res) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.IDU_NO_DATA);
                                msg.setDetail("deleteStorageRepository");
                                return msg;
                        }
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * 删除文件
         * 
         * [参数列表所需参数]
         * uuid: 文件的uuid
         */
        @Override
        public Message removeFile() {
                return this.inline_removeFile((String) parameter.get("uuid"));
        }

        /**
         * 删除文件（内部调用）
         * 
         * @param uuid 文件的uuid
         * @param flip 是否取反
         *             true: 是
         *             false: 否 
         * @return Message对象
         */
        private Message inline_checkFileExist(String uuid, boolean flip) {
                Message msg = new Message();
                HashMap<String, Object> p = null;
                String sql = null;
                Element sqlRoot = SqlRepository.get("file_storage");
                if (null == sqlRoot) {
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail("file_storage");
                        return msg;
                }
                try {
                        p = new HashMap<String, Object>();
                        p.put("uuid", uuid);
                        sql = DatabaseKit.composeSql(sqlRoot, (String) parameter.get("selectStorageFile"), p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail((String) parameter.get("selectStorageFile"));
                                return msg;
                        }
                        ArrayList<HashMap<String, Object>> list = DbFactory.select(this.connection, sql);
                        if (0 < list.size()) {
                                if (!flip) {
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("File Exist");
                                } else {
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("File Not Exist");
                                }
                        } else {
                                if (!flip) {
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("File Not Exist");
                                } else {
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("File Exist");
                                }
                        }
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * 检查文件是否存在
         * 
         * [参数列表所需参数]
         * uuid: 文件的uuid
         */
        @Override
        public Message checkFileExist() {
                return this.inline_checkFileExist((String) parameter.get("uuid"), false);
        }

        /**
         * 检查文件是否不存在
         * 
         * [参数列表所需参数]
         * uuid: 文件的uuid
         */
        @Override
        public Message checkFileNotExist() {
                return this.inline_checkFileExist((String) parameter.get("uuid"), true);
        }

        /**
         * 修改附件
         * [要求]
         * 待修改的updateSqlId中，需要sn_附件列名的参数，并且条件设置为，如果为null，那么清空内容。
         * 
         * [参数列表所需参数]
         * idColumnName: 附件所在数据的唯一标记（通常为uuid，用于定位待修改记录）
         * idColumnValue: 附件所在数据的唯一标记对应的值（uuid对应的值，用于定位待修改记录）
         * moduleName: 模块的名称
         * selectSqlId: 模块查询Sql的id（用于定位待修改记录）
         * updateSqlId: 模块更新Sql的id（用于更新修改内容）
         * attachmentColumnName: 附件的列名（用于更新修改内容）
         * newAttachments: 新附件的集合（用于更新修改内容）
         */
        @Override
        public Message modifyAttachment() {
                Message msg = new Message();
                HashMap<String, Object> p = null;
                int res = 0;
                String sql = null;
                Element sqlRoot = SqlRepository.get((String) parameter.get("moduleName"));
                if (null == sqlRoot) {
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail((String) parameter.get("moduleName"));
                        return msg;
                }
                try {
                        // 获取附件所在的数据
                        p = new HashMap<String, Object>();
                        p.put((String) parameter.get("idColumnName"), parameter.get("idColumnValue"));
                        sql = DatabaseKit.composeSql(sqlRoot, (String) parameter.get("selectSqlId"), p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail((String) parameter.get("selectSqlId"));
                                return msg;
                        }
                        ArrayList<HashMap<String, Object>> list = DbFactory.select(this.connection, sql);
                        if (0 >= list.size()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.QUERY_NO_DATA);
                                msg.setDetail((String) parameter.get("selectSqlId"));
                                return msg;
                        }
                        HashMap<String, Object> data = list.iterator().next();
                        // “旧”附件
                        String oldAttachments = (String) data.get((String) parameter.get("attachmentColumnName"));
                        // “新”附件
                        // 如果“旧”附件不为空，那么判断是否有效，无效则清空该项数据。
                        if (null != oldAttachments) {
                                if ((null == parameter.get("newAttachments"))) {
                                        String oldAttachment[] = oldAttachments.split(";");
                                        for (int i = 0; i < oldAttachment.length; i++) {
                                                // 删除“旧”附件
                                                Message m = this.inline_removeFile(oldAttachment[i]);
                                                if (m.getStatus() != Message.STATUS.SUCCESS) {
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.OTHER);
                                                        msg.setDetail("File Remove Error [" + oldAttachment[i] + "]");
                                                        return msg;
                                                }
                                                // 清空“旧”附件
                                                p = new HashMap<String, Object>();
                                                p.put((String) parameter.get("idColumnName"), parameter.get("idColumnValue"));
                                                p.put("sn_" + (String) parameter.get("attachmentColumnName"), null);
                                                sql = DatabaseKit.composeSql(sqlRoot, (String) parameter.get("updateSqlId"), p);
                                                if (0 >= sql.trim().length()) {
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                                        msg.setDetail((String) parameter.get("updateSqlId"));
                                                        return msg;
                                                }
                                                res = DbFactory.iduExecute(this.connection, sql);
                                                if (1 > res) {
                                                        // 清空“旧”附件失败
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.OTHER);
                                                        msg.setDetail("File Remove Error [" + (String) parameter.get("updateSqlId") + "]");
                                                        return msg;
                                                }
                                        }
                                } else {
                                        String oldAttachment[] = oldAttachments.split(";");
                                        String newAttachment[] = ((String) parameter.get("newAttachments")).split(";");
                                        for (int i = 0; i < oldAttachment.length; i++) {
                                                boolean hasSameFile = false;
                                                for (int j = 0; j < newAttachment.length; j++) {
                                                        if (oldAttachment[i].equalsIgnoreCase(newAttachment[j])) {
                                                                hasSameFile = true;
                                                                break;
                                                        }
                                                }
                                                if (hasSameFile) {
                                                        continue;
                                                }
                                                // 删除“旧”附件
                                                Message m = this.inline_removeFile(oldAttachment[i]);
                                                if (m.getStatus() != Message.STATUS.SUCCESS) {
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.OTHER);
                                                        msg.setDetail("File Remove Error [" + oldAttachment[i] + "]");
                                                        return msg;
                                                }
                                                // 清空“旧”附件
                                                p = new HashMap<String, Object>();
                                                p.put((String) parameter.get("idColumnName"), parameter.get("idColumnValue"));
                                                p.put("sn_" + (String) parameter.get("attachmentColumnName"), null);
                                                sql = DatabaseKit.composeSql(sqlRoot, (String) parameter.get("updateSqlId"), p);
                                                if (0 >= sql.trim().length()) {
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                                        msg.setDetail((String) parameter.get("updateSqlId"));
                                                        return msg;
                                                }
                                                res = DbFactory.iduExecute(this.connection, sql);
                                                if (1 > res) {
                                                        // 清空“旧”文件集群数据失败
                                                        msg.setStatus(Message.STATUS.ERROR);
                                                        msg.setError(Message.ERROR.OTHER);
                                                        msg.setDetail("File Remove Error [" + (String) parameter.get("updateSqlId") + "]");
                                                        return msg;
                                                }
                                        }
                                }
                        }
                        if (((null == oldAttachments) && (null == parameter.get("newAttachments"))) || ((null != oldAttachments) && (null == parameter.get("newAttachments")))) {
                                // （如果“旧”附件为空，且“新”附件为空，不做任何操作）
                                // （如果“旧”附件不为空，且“新”附件为空，执行清空“旧”附件的操作，上面已实现）
                        } else if (((null == oldAttachments) && (null != parameter.get("newAttachments"))) || ((null != oldAttachments) && (null != parameter.get("newAttachments")))) {
                                // （如果“旧”附件为空，且“新”附件不为空，判断“新”附件是否合法，然后更新数据）
                                // （如果“旧”附件不为空，且“新”附件不为空，且在两者不相等的情况下，判断“新”附件是否合法，执行删除“旧”附件的操作，并更新集群数据为“新”附件集群）
                                // 判断“新”附件是否合法
                                String newAttachment[] = ((String) parameter.get("newAttachments")).split(";");
                                for (int i = 0; i < newAttachment.length; i++) {
                                        Message m = this.inline_checkFileExist(newAttachment[i], false);
                                        if (m.getStatus() != Message.STATUS.SUCCESS) {
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.OTHER);
                                                msg.setDetail("File Not Exist [" + newAttachment[i] + "]");
                                                return msg;
                                        }
                                        m = this.inline_savePermanentFile(newAttachment[i]);
                                        if (m.getStatus() != Message.STATUS.SUCCESS) {
                                                msg.setStatus(Message.STATUS.ERROR);
                                                msg.setError(Message.ERROR.OTHER);
                                                msg.setDetail("Save File Permanent Error [" + newAttachment[i] + "]");
                                                return msg;
                                        }
                                }
                                // 更新数据为“新”附件
                                p = new HashMap<String, Object>();
                                p.put((String) parameter.get("idColumnName"), parameter.get("idColumnValue"));
                                p.put((String) parameter.get("attachmentColumnName"), parameter.get("newAttachments"));
                                sql = DatabaseKit.composeSql(sqlRoot, (String) parameter.get("updateSqlId"), p);
                                if (0 >= sql.trim().length()) {
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                        msg.setDetail((String) parameter.get("updateSqlId"));
                                        return msg;
                                }
                                res = DbFactory.iduExecute(this.connection, sql);
                                if (1 > res) {
                                        // 清空“旧”附件失败
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("File Modify Error [" + (String) parameter.get("updateSqlId") + "]");
                                        return msg;
                                }
                        }
                        // 操作成功
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }

        /**
         * 删除附件
         * 
         * [参数列表所需参数]
         * idColumnName: 附件所在数据的唯一标记（通常为uuid，用于定位待修改记录）
         * idColumnValue: 附件所在数据的唯一标记对应的值（uuid对应的值，用于定位待修改记录）
         * moduleName: 模块的名称
         * selectSqlId: 模块查询Sql的id（用于定位待修改记录）
         * attachmentColumnName: 附件的列名（用于更新修改内容）
         */
        @Override
        public Message removeAttachment() {
                Message msg = new Message();
                HashMap<String, Object> p = null;
                String sql = null;
                Element sqlRoot = SqlRepository.get((String) parameter.get("moduleName"));
                if (null == sqlRoot) {
                        msg.setStatus(Message.STATUS.ERROR);
                        msg.setError(Message.ERROR.NO_MODULE_SQL);
                        msg.setDetail((String) parameter.get("moduleName"));
                        return msg;
                }
                try {
                        // 获取附件所在的数据
                        p = new HashMap<String, Object>();
                        p.put((String) parameter.get("idColumnName"), parameter.get("idColumnValue"));
                        sql = DatabaseKit.composeSql(sqlRoot, (String) parameter.get("selectSqlId"), p);
                        if (0 >= sql.trim().length()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.COMPOSE_SQL_ERROR);
                                msg.setDetail((String) parameter.get("selectSqlId"));
                                return msg;
                        }
                        ArrayList<HashMap<String, Object>> list = DbFactory.select(this.connection, sql);
                        if (0 >= list.size()) {
                                msg.setStatus(Message.STATUS.ERROR);
                                msg.setError(Message.ERROR.QUERY_NO_DATA);
                                msg.setDetail((String) parameter.get("selectSqlId"));
                                return msg;
                        }
                        HashMap<String, Object> data = list.iterator().next();
                        // “旧”附件
                        String oldAttachments = (String) data.get((String) parameter.get("attachmentColumnName"));
                        String oldAttachment[] = oldAttachments.split(";");
                        for (int i = 0; i < oldAttachment.length; i++) {
                                // 删除“旧”附件
                                Message m = this.inline_removeFile(oldAttachment[i]);
                                if (m.getStatus() != Message.STATUS.SUCCESS) {
                                        msg.setStatus(Message.STATUS.ERROR);
                                        msg.setError(Message.ERROR.OTHER);
                                        msg.setDetail("File Remove Error [" + oldAttachment[i] + "]");
                                        return msg;
                                }
                        }
                        // 操作成功
                        msg.setStatus(Message.STATUS.SUCCESS);
                        msg.setError(Message.ERROR.NONE);
                        return msg;
                } catch (Exception e) {
                        Framework.LOG.warn(Config.MODULE_NAME, e.toString());
                        msg.setStatus(Message.STATUS.EXCEPTION);
                        msg.setError(Message.ERROR.OTHER);
                        msg.setDetail(e.toString());
                        return msg;
                }
        }
}