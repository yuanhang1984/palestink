"use strict";

class Assistant {
  /**
   * 根据参数名从url获取参数值
   * @param name 参数名
   * @return 返回参数名对应的值值,没有找到返回null.
   */
  static getQueryFromUrl(name) {
       let reg = new RegExp(`(^|&)${name}=([^&]*)(&|$)`);
       let result = window.location.search.substr(1).match(reg);
       if (null != result) {
         let value = result[2];
         if (0 >= value.length) {
           return null;
         }
         return unescape(value);
       } else {
         return null;
       }
  }

  /**
   * 判断json的对象是否含有数据
   * @param obj json对象
   * @return true: 有数据
   *         false: 无数据
   */
  static jsonExistData(obj) {
    for (let key in obj) {
      return true;
    }
    return false;
  }
}
