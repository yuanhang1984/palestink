"use strict";

class Configure {
  /**
   * 获取项目路径(通过参数切换测试环境和正式环境)
   * @param code
   *        1: 返回测试服务器项目路径.
   *        2: 返回正式服务器项目路径.
   *        其他: 返回null.
   * @return 返回项目路径
   */
  static getProjectPath(code) {
    if (1 == code) {
      return "http://192.168.1.131:8080/lego/";
    } else if (2 == code) {
      return "http://47.92.152.242:8080/lego/";
    } else {
      return null;
    }
  }
}
