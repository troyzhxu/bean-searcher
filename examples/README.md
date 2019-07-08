# bean-searcher-demo

### 介绍
本项目主要用于演示 bean-searcher 在Web工程中的使用方式，以及展示在列表检索场景中bean-searcher可帮助我们解决的问题。

### 软件架构
软件架构说明

- 控制层：spring-boot-web 2
- 数据库：h2 （无需安装配置）
- 数据库访问：spring-data-jpa、bean-searcher
- 模板引擎：thymeleaf
- 前端框架：vue、element-ui

### 运行项目

1. git clone https://gitee.com/ejlchina-zhxu/bean-searcher-demo.git
2. cd bean-searcher-demo
3. mvn spring-boot:run

### 运行效果

##### 1. 按照上述方式运行项目，若控制台有如下输出表示运行成功：
![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/013035_4312bf3f_1393412.png "微信截图_20190707013006.png")
##### 2. 打开浏览器访问：http://localhost:8080/ 页面如下：
![输入图片说明](https://images.gitee.com/uploads/images/2019/0706/235039_e6e083ac_1393412.png "微信图片_20190706235025.png")

##### 3. 如上图，本示例展示了一个简单的员工列表页面，实现了如下功能：
- 按姓名、年龄、部门、入职时间 的组合条件进行后端检索，每个检索项都可以选择不同的检索方式，如下图：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/000951_30cdf845_1393412.png "微信图片_20190707000918.png")
![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/001118_486c1630_1393412.png "微信图片_20190707001103.png")

- 按ID、姓名、年龄、部门、入职时间 进行后端排序：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/001446_6f20c483_1393412.png "微信截图_20190707001420.png")

- 分页功能：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/001526_71503b29_1393412.png "微信截图_20190707001512.png")  


OK，页面做的虽然粗糙，但是一个列表检索的功能基本上展示了，下面主要看下在后端bean-searcher是如何简化我们的开发。

### 代码分析

##### 控制层代码
有同学看到这会想，若要实现以上演示的的可以按照各种条件组合检索、排序、分页的功能，那后端的代码量至少也得上百行吧。bean-searcher 告诉你，不用，关键代码，就两行！啥？我怎么不信？请看代码：
![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/002627_6e86d808_1393412.png "微信截图_20190707002555.png")

检索条件呢？检索方式呢？排序呢？分页呢？通通都交给 bean-sarcher 去实现啦，世界突然如此美好！

##### 检索实体类

细心的同学会发现在上述代码里用到一个 Employee 这个类。没错，它就是用来告诉 bean-searcher 如何与数据库字段映射的一个实体类：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/004251_e197c57e_1393412.png "微信图片_20190707004229.png") 

##### 检索器配置

在 bean-searcher 2 版本中，bean-searcher 已经实现了 spring-boot-starter 化，所在，在spring-boot 项目里，它只需要在application.properties里配几个必须信息即可
![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/012140_aa87c91d_1393412.png "微信图片_20190707012120.png")
在IDEA里，bean-searcher的配置是有提示的哦：
![输入图片说明](https://images.gitee.com/uploads/images/2019/0707/012532_e81dee9c_1393412.png "微信图片_20190707012518.png")


### 总结

- bean-searcher 设计的目标并不是替代某个ORM框架，它只是为了弥补现有ORM框架在复杂列表检索中的不便，实际项目中，配合使用它们，效果或会更好。
- 本例只是 bean-searcher 在联表检索中的一个简单的演示，更多用法，请参阅: https://gitee.com/ejlchina-zhxu/bean-searcher/wikis/Home (文档持续完善中)
- 看完这些，大家有没有觉得 bean-searcher 很实用又很简单呢？如果是，就点个Star吧 ^_^

### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request


### 码云特技

1. 使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2. 码云官方博客 [blog.gitee.com](https://blog.gitee.com)
3. 你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解码云上的优秀开源项目
4. [GVP](https://gitee.com/gvp) 全称是码云最有价值开源项目，是码云综合评定出的优秀开源项目
5. 码云官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6. 码云封面人物是一档用来展示码云会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)