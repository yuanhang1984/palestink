"use strict";

class JSTextField {
  /*
   * 构造函数（无参）
   * 自动初始化对象的id。
   */
  constructor() {
    this.objectId = JString.getUuid(true);
    this.objectType = "";
    this.objectPlaceHolder = "";
    this.objectValue = "";
    this.objectReadOnly = false;
    this.objectClass = "";
    this.objectCode = "";
  }

  getId() {
    return this.objectId;
  }

  getObject() {
    return $("#" + this.getId());
  }

  setType(type) {
    this.objectType = type;
  }

  getType() {
    return this.objectType;
  }

  setPlaceHolder(placeHolder) {
    this.objectPlaceHolder = placeHolder;
  }

  getPlaceHolder() {
    return this.objectPlaceHolder;
  }

  setValue(value) {
    this.objectValue = value;
  }

  getValue() {
    return this.objectValue;
  }

  setClass(clazz) {
    this.objectClass = clazz;
  }

  getClass() {
    return this.objectClass;
  }

  setReadOnly(readOnly) {
    this.objectReadOnly = readOnly;
  }

  getReadOnly() {
    return this.objectReadOnly;
  }

  getCode() {
    return this.objectCode;
  }

  /* 
   * 生成源码
   *
   * setType 设置类型
   *   text 文本框
   *   password 密码框
   * setPlaceHolder 设置placeholder
   */
  generateCode() {
    let readOnly = "";
    if (this.getReadOnly()) {
      readOnly = "readonly";
    }
    // 注意：本类样式一定要加在默认class之后，getClass()之前。
    this.objectCode = `
      <input ${readOnly} type = "${this.getType()}" class = "form-control JSTextField ${this.getClass()}" id = "${this.getId()}" placeholder = "${this.getPlaceHolder()}"  value = "${this.getValue()}" />
    `;
  }
}
