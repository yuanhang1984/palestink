"use strict";

class Ajax {
  /**
   * 提交
   * @param url 请求地址
   * @param data 参数数据
   * @param async
   *        true: 异步
   *        false: 同步
   * @param withCredentials
   *        true: 自带证书
   *        false: 不带证书
   * @param multipartUpload
   *        true: 附件模式
   *        false: 文本模式
   */
  static submit(url, data, async, withCredentials, multipartUpload) {
    let result = null;
    let isCrossDomain = false;;
    if (-1 != window.location.protocol.indexOf("http")) {
      isCrossDomain = false;
    } else {
      isCrossDomain = true;
    }
    let contentType = "application/x-www-form-urlencoded";
    let processData = true;
    if (multipartUpload) {
      contentType = false;
      processData = false;
    }
    $.ajax({
      // 请求方式
      "type": "post",
      // 同步or异步
      "async": async,
      // 请求地址
      "url": url,
      // 参数数据
      "data": data,
      // 数据类型
      "dataType": "json",
      // 是否自带证书
      "xhrFields": {
          "withCredentials": withCredentials
      },
      // 内容类型
      "contentType": contentType,
      // 是否以contentType的默认值传递数据
      "processData": processData,
      // 是否跨域
      //  crossDomain: isCrossDomain,
      // 操作成功后的返回结果
      "success": function(r) {
          result = r;
      }
    });
    return result;
  }
}
