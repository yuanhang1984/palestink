CREATE TABLE `demo_employee-info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `uuid` varchar(64) NOT NULL COMMENT 'uuid',
  `name` varchar(32) NOT NULL COMMENT '姓名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `sex` int(11) DEFAULT NULL COMMENT '性别\n男：1\n女：0',
  `code` varchar(32) DEFAULT NULL COMMENT '身份证号码',
  `address` varchar(256) DEFAULT NULL COMMENT '家庭住址',
  `remark` varchar(256) DEFAULT NULL COMMENT '备注',
  `create_datetime` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='示例_雇员信息';
