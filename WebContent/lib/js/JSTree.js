"use strict";

class JSTree {
  /*
   * 构造函数（无参）
   * 自动初始化对象的id。
   */
  constructor() {
    this.objectId = JString.getUuid(true);
    this.objectNode = new Array();
    this.objectLinkTarget = "_self";
    this.objectClass = "";
    this.objectCode = "";
  }

  getId() {
    return this.objectId;
  }

  getObject() {
    return $("#" + this.getId());
  }

  setLinkTarget(target) {
    this.objectLinkTarget = target;
  }

  getLinkTarget() {
    return this.objectLinkTarget;
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
   * node 为json对象数组，格式如下：
   * [
   *   {
   *     "text": "山东",
   *     "icon": "icon-cogs",
   *     "list": [
   *       {
   *         "text": "济南",
   *         "icon": "icon-cogs",
   *         "link": "http://www.baidu.com"
   *       },
   *       {
   *         "text": "聊城",
   *         "link": "http://www.baidu.com"
   *       },
   *       {
   *         "text": "菏泽",
   *         "icon": "icon-cogs",
   *         "link": "http://www.baidu.com"
   *       },
   *       {
   *         "text": "临沂",
   *         "link": "http://www.baidu.com"
   *       }
   *     ]
   *   },
   *   {
   *     "text": "陕西"
   *   },
   *   {
   *     "text": "河北"
   *   },
   *   {
   *     "text": "辽宁"
   *   }
   * ]
   * text 导航显示的文本
   * icon 显示图标
   * link 链接地址
   * list 其下的数组
   */
  setNode(node) {
    this.objectNode = node;
  }

  getNode() {
    return this.objectNode;
  }

  update() {
    // 绑定事件
    let _this = this;
    $(`#${this.getId()}`).find("a").click(function() {
      $(`#${_this.getId()}`).find("li").removeClass("active");
      $(this).parent().addClass("active");
    });
  }

  /* 
   * 遍历节点
   */
  _eachNode(node) {
    let ulCode = "";
    if (this._firstUl) {
      // 注意：本类样式一定要加在默认class之后，getClass()之前。
      ulCode = `<ul class = "JSTree ${this.getClass()}" id = "${this.getId()}">`;
      this._firstUl = false;
    } else {
      ulCode = "<ul>";
    }
    // debugger;
    for (let i = 0; i < node.length; i++) {
      let obj = node[i];
      let iCode = "";
      if (undefined != obj.icon) {
        iCode = `<i class = "${obj.icon}"></i>`;
      }
      if (undefined != obj.link) {
        // 子菜单
        ulCode += `<li><a href = "${obj.link}" target = "${this.getLinkTarget()}">${iCode}${obj.text}</a></li>`;
      } else {
        // 父菜单
        ulCode += `<li><span>${iCode}${obj.text}</span></li>`;
      }
      let subNode = "";
      if ((undefined != obj.list) && (0 < obj.list.length)) {
        subNode += "<ul>";
        subNode = this._eachNode(obj.list);
      }
      ulCode += subNode;
    }
    ulCode += "</ul>";
    return ulCode;
  }

  /* 
   * setNode 设置菜单时，为了显示效果最多二级菜单。
   */
  generateCode() {
    this._firstUl = true;
    // 生成代码
    this.objectCode = this._eachNode(this.getNode());
  }
}
