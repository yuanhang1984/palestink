"use strict";

class JSCheckBox {
  /*
   * 构造函数（无参）
   * 自动初始化对象的id。
   */
  constructor() {
    this.objectId = JString.getUuid(true);
    this.objectEnabled = true;
    this.objectName = "";
    this.objectLabel = "";
    this.objectValue = "";
    this.objectSelected = false;
    this.objectClass = "";
    this.objectCode = "";
  }

  getId() {
    return this.objectId;
  }

  getObject() {
    return $("#" + this.getId());
  }

  setEnabled(enabled) {
    this.objectEnabled = enabled;
  }

  getEnabled() {
    return this.objectEnabled;
  }

  setName(name) {
    this.objectName = name;
  }

  getName() {
    return this.objectName;
  }

  setLabel(label) {
    this.objectLabel = label;
  }

  getLabel() {
    return this.objectLabel;
  }

  setValue(value) {
    this.objectValue = value;
  }

  getValue() {
    return this.objectValue;
  }

  setSelected(selected) {
    this.objectSelected = selected;
  }

  getSelected() {
    return this.objectSelected;
  }

  setClass(clazz) {
    this.objectClass = clazz;
  }

  getClass() {
    return this.objectClass;
  }

  getCode() {
    return this.objectCode;
  }

  generateCode() {
    let selectedCode = "";
    if (this.getSelected()) {
      selectedCode = "checked";
    }
    let enabledCode = "";
    if (!this.getEnabled()) {
      enabledCode = "disabled";
    }
    let labelCode = "";
  if ("" != this.getLabel()) {
      labelCode = `<label for = "${this.getId()}">${this.getLabel()}</label>`;
    }
    // 注意：本类样式一定要加在默认class之后，getClass()之前。
    this.objectCode = `
      <input ${selectedCode} ${enabledCode} type = "checkbox" class = "JSCheckBox ${this.getClass()}" id = "${this.getId()}" name = "${this.getName()}" value = "${this.getValue()}" />${labelCode}
    `;
  }
}
