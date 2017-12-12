[Q] 部署服务需要更改的地方
[A] 除本文以下涵盖内容之外，还需要修改相关配置，包括：
    1. legoes中web.xml的数据库连接信息。
    2. lego_storage模块中config.xml文件的上传路径。

[Q] Tomcat为服务器时的文件上传限制
[A] Tomcat提供的post服务，默认接收数据为2MB，如果用post的方式上传文件大于2MB时将会出错。需要修改post的大小限制。
    在${TOMCAT_HOME}/conf/server.xml文件中Connector标签中，增加maxPostSize属性。当maxPostSize<=0时，post接收数据没有尺寸限制。如：
    <Connector port="8081" maxThreads="150" minSpareThreads="25" maxSpareThreads="75" enableLookups="false" redirectPort="8443" acceptCount="100" debug="0" connectionTimeout="20000" disableUploadTimeout="true" URIEncoding="GBK" maxPostSize="0"/>
    （maxPostSize参数只有当request的Content-Type为“application/x-www-form-urlencoded”时起作用[未经验证]）

[Q] 关闭Tomcat热加载
[A] palestink框架为“动态资源加载方式”，这意味着不能“热加载”，需要关闭Tomcat的reloadable。方法有二：
    1. 在${TOMCAT_HOME}/conf/server.xml文件中，修改Host下的Context，增加reloadable="false"属性。
    2. 新建${TOMCAT_HOME}/webapps/${PROJECT}/META-INF/context.xml文件，内容如下：
    <?xml version="1.0" encoding="UTF-8"?>
    <Context reloadable = "false"></Context>

[Q] Url中包含特殊字符造成的异常（IllegalArgumentException: Invalid character found in the request target. The valid characters are defined in RFC 7230 and RF.）
[A] Tomcat在7.0.73、8.0.39、8.5.7版本后，增加了对于http头的验证。如果提交的Url中包含未转义的特殊字符将会报错。需要在${TOMCAT_HOME}/conf/catalina.properties文件中添加或者修改为：
    tomcat.util.http.parser.HttpParser.requestTargetAllow=|{}

[Q] 启动Tomcat出现JDK版本过低问题
[A] 如果项目编译用了较Tomcat过高的JDK版本，在启动Tomcat时会报类似“unsupported major.minor version 52.0”错误。解决方法是：
    1. 在服务器上下载并安装最新的JDK。
    2. 修改Tomcat的catalina.sh文件，在#!/bin/sh下增加一行，写入：
       JAVA_HOME=/home/jack/Program/jdk1.8.0_144
