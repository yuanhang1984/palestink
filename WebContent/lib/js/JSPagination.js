"use strict";

class JSPagination {
  /*
   * 构造函数（无参）
   * 自动初始化对象的id。
   */
  constructor() {
    this.objectId = JString.getUuid(true);
    this.objectOffset = 0;
    this.objectLimit = 0;
    this.objectCount = 0;
    this.objectSize = 0;
    this.objectCode = "";
  }

  getId() {
    return this.objectId;
  }

  getObject() {
    return $("#" + this.getId());
  }

  setOffset(offset) {
    this.objectOffset = offset;
  }

  getOffset() {
    return this.objectOffset;
  }

  setLimit(limit) {
    this.objectLimit = limit;
  }

  getLimit() {
    return this.objectLimit;
  }

  setCount(count) {
    this.objectCount = count;
  }

  getCount() {
    return this.objectCount;
  }

  setSize(size) {
    this.objectSize = size;
  }

  getSize() {
    return this.objectSize;
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
   * setOffset 设置offset（MySql数据库Select操作offset参数）
   * setLimit 设置limit（MySql数据库Select操作limit参数）
   * setCount 设置count（数据库检索结果的总条目数）
   * setSize 设置size（分几页）
   */
  generateCode() {
    let currentPage = 0;
    if (0 >= this.getOffset()) {
      currentPage = 1;
    } else {
      currentPage = Math.ceil(this.getOffset() / this.getLimit()) + 1;
    }
    let count = Math.ceil(this.getCount() / this.getLimit());
    let displaySceneCount = Math.ceil(count / this.getSize());
    let currentPageSceneNum = Math.ceil(currentPage / this.getSize());
    let leftBtnCode = "";
    if (currentPageSceneNum > 1) {
      leftBtnCode = `
        <li data-offset = "${((currentPageSceneNum - 1) * this.getSize() * this.getLimit()) - this.getLimit()}"><a><span>&laquo;</span></a></li>
      `;
    }
    let otherCode = "";
    for (let i = ((currentPageSceneNum * this.getSize()) - this.getSize() + 1); i <= (currentPageSceneNum * this.getSize()); i++) {
      if (i > count) {
        break;
      }
      if (i == (currentPage)) {
        otherCode += `
          <li data-offset = "${(i * this.getLimit() - this.getLimit())}" class = "active"><a>${i}</a></li>
        `;
      } else {
        otherCode += `
          <li data-offset = "${(i * this.getLimit() - this.getLimit())}"><a>${i}</a></li>
        `;
      }
    }
    if ((displaySceneCount - currentPageSceneNum) >= 1) {
        otherCode += `
          <li data-offset = "${(currentPageSceneNum * this.getSize() * this.getLimit())}"><a><span>&raquo;</span></a></li>
        `;
    }
    // 注意：本类样式一定要加在默认class之后，getClass()之前。
    this.objectCode = `
      <nav>
        <ul class = "pagination JSPagination ${this.getClass()}">
          ${leftBtnCode}
          ${otherCode}
        </ul>
      </nav>
    `;
  }
}
