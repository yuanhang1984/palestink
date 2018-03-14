"use strict";

class JString {
  /**
   * 获取uuid
   * @param removeLine(默认为false)
   *        true: 返回不含中划线的uuid.
   *        false: 返回带有中划线的uuid.
   * @return uuid
   */
  static getUuid(removeLine = false) {
    let s = [];
    let hexDigits = "0123456789abcdef";
    for (let i = 0; i < 36; i++) {
      s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
    }
    s[14] = "4";
    s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);
    if (!removeLine) {
      s[8] = s[13] = s[18] = s[23] = "-";
    }
    let uuid = s.join("");
    return uuid;
  }
}
