# 模块创建指导  

+ 确定模块名称  
比如，新建“通讯录”模块，名称确认为contact_book。
> 注意：名称中避免空格、大小写，以“_”连接各个单词。且模块名称不可重复。

+ 新建模块目录  
在WEB-INF\module\目录下创建名称为contact_book的文件夹。

+ 创建模块目录树  
contact_book  
--bin（目录）  
--lib（目录）  
--res（目录）  
----config.xml  
----db.sql  
----dispatch.xml  
----sql.xml  
--src（目录）  
----contact_book（目录）  
------necessary（目录）  
--------Config.java  
--------Custom.java  
--------Daemon.java  
------optional（目录）  
