package framework.sdk.spec.module;

import framework.sdk.msg.Message;

public interface FileStorage {

        /*
         * 上传临时文件
         */
        public abstract Message uploadTemporaryFile();

        /*
         * 获取文件
         */
        public abstract Message getFile();

        /*
         * 保存为正式文件
         */
        public abstract Message savePermanentFile();

        /*
         * 删除文件
         */
        public abstract Message removeFile();

        /*
         * 检查文件是否存在
         */
        public abstract Message checkFileExist();

        /*
         * 检查文件是否不存在
         */
        public abstract Message checkFileNotExist();

        /*
         * 修改附件
         */
        public abstract Message modifyAttachment();

        /*
         * 删除附件
         */
        public abstract Message removeAttachment();
}