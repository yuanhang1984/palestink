"use strict";

class JSUploadList {
  /*
   * 构造函数（无参）
   * 自动初始化对象的id。
   */
  constructor() {
    this.objectId = JString.getUuid(true);
    this.objectList = new Array();
    this.objectClass = "";
    this.objectCode = "";
  }

  getId() {
    return this.objectId;
  }

  getObject() {
    return $("#" + this.getId());
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

  setList(list) {
    this.objectList = list;
  }

  getList() {
    return this.objectList;
  }

  /* 
   * item 为json对象，格式如下：
   * {
   *   "key": "12123",
   *   "name": "新建文本文件.txt",
   *   "size": "380"
   * }
   * 
   * key 标记（通常为id值）
   * name 文件名
   * size 文件尺寸（kb）
   */
  addItem(item) {
    this.objectList.push(item);
  }

  insertItemAt(item, index) {
    this.objectList.splice(index, 0, item);
  }

  removeItemAt(index) {
    this.objectList.splice(index, 1);
  }

  getItemAt(index) {
    if (index <= this.objectList.length) {
      return this.objectList[index];
    }
  }

  getListCount() {
    return this.objectList.length;
  }

  update() {
    $("#" + this.getId()).find("li").find("i").click(function() {
      $(this).parent().remove();
    });
  }

  generateCode() {
    let fileListCode = "";
    for (let i = 0; i < this.getListCount(); i++) {
      let obj = this.objectList[i];
      fileListCode += `
        <li class = "list-group-item"><span>${obj.name}</span><span>${obj.size}</span><i class = "icon-remove"></i></li>
      `;
    }
    // 注意：本类样式一定要加在默认class之后，getClass()之前。
    this.objectCode = `
      <ul class = "list-group JSUploadList ${this.getClass()}" id = "${this.getId()}">
        <a class = "list-group-item active">Choose File</a>
        ${fileListCode}
      </ul>
    `;
  }
}
