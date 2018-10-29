#activitiAdmin项目

使用的技术主要是：
````
maven+springboot+jpa+lombok+activiti+log4j2

thymeleaf+bootstrap+jquery

mysql+tomcat
````

rest接口文档生成是：
````
swagger2
````

web后台主要实现的功能是：
````
系统管理：
    用户管理
    修改密码

基础管理：
    流程部署管理
    流程定义管理

业务管理：
    添加用户申请
    
任务管理：
    待办任务管理
    已办任务管理
    
简单流程说明:
员工->添加用户申请提交,管理员->代办任务管理,进行审核
具体查看代办的角色,根据bpmn的流程图配置,这里要和数据库的角色表同步
 ````   

使用方式是：

首先:

打开数据库,执行初始化SQL,建库建表,修改application.properties的数据库连接

其次:

启动项目,浏览器输入网址：http://localhost:8080/index进入首页
未登录情况下，会自动跳转到登录页面

````
测试账号(格式为：编号 / 密码)：

管理员：sjb / 123

员工：wangba / 123
````

rest API 接口查看地址是：
````
http://localhost:8080/rest/api/doc/index.html#/
````

项目打包方式为jar
````
上传到服务器指定地址后，执行：java -jar admin.jar 即可
````

配置文件说明：
````
application.properties

包含thymeleaf,datasource,jpa,jackson,tomcat的信息
````