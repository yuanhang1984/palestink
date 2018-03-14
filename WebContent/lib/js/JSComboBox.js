"use strict";

class JSComboBox {
  /*
   * 构造函数（无参）
   * 自动初始化对象的id。
   */
  constructor() {
    this.objectId = JString.getUuid(true);
    this.objectType = "dropdown";
    this.objectSelectedIndex = 0;
    this.objectItemList = new Array();
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

  setSelectedIndex(selectedIndex) {
    this.objectSelectedIndex = selectedIndex;
  }

  getSelectedIndex() {
    return this.objectSelectedIndex;
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

  /* 
   * item 为json对象，格式如下：
   * {
   *   "type": "option",
   *   "text": "-- select --",
   *   "value": "0",
   *   "enable": false
   * }
   * type 显示的类型
   *      option 常规下拉选项
   *      separator 分割线（当type为separator时，后面参数忽略）
   * text 显示的文本
   * value 对应的值
   * enable 是否可用
   *        true 可用
   *        false 禁用
   */
  addItem(item) {
    this.objectItemList.push(item);
  }

  insertItemAt(item, index) {
    this.objectItemList.splice(index, 0, item);
  }

  removeItemAt(index) {
    this.objectItemList.splice(index, 1);
  }

  getItemAt(index) {
    if (index <= this.objectItemList.length) {
      return this.objectItemList[index];
    }
  }

  getItemCount() {
    return this.objectItemList.length;
  }

  update() {
    // 绑定事件
    let _this = this;
    $(`#${this.getId()}`).find("ul").find("li").find("a").click(function() {
      if (!$(this).parent().hasClass("disabled")) {
        $(`#${_this.getId()}`).find("button").find("span:nth-child(1)").html($(this).html());
        $(`#${_this.getId()}`).find("button").attr("data-value", $(this).parent().attr("data-value"));
      }
    });
  }

  /* 
   * setType 设置菜单向上还是向下
   *         dropup 菜单向上
   *         dropdown 菜单向下
   */
  generateCode() {
    // 遍历list组合代码
    let liCode = "";
    for (let i = 0; i < this.objectItemList.length; i++) {
      let obj = this.objectItemList[i];
      if ("separator" == obj.type) {
        // 分割线
        liCode += `<li class = "divider"></li>`;
      } else {
        let disableClass = "";
        if (!obj.enable) {
          disableClass = "disabled";
        }
        // 常规下拉选项
        liCode += `<li class = "${disableClass}" data-value = "${obj.value}"><a>${obj.text}</a></li>`;
      }
    }
    // 注意：本类样式一定要加在默认class之后，getClass()之前。
    this.objectCode = `
      <div class = "JSComboBox ${this.getClass()} ${this.getType()}" id = "${this.getId()}">
        <button class = "btn btn-default dropdown-toggle" type = "button" data-toggle = "dropdown" data-value = "">
          <span>${this.getItemAt(this.getSelectedIndex()).text}</span><span class = "caret"></span>
        </button>
        <ul class = "dropdown-menu">${liCode}</ul>
      </div>
    `;
  }
}
