---
description: IOT 物联网 IOTCP 协议 SDK 集成
---

# 起步

## 安装

### Maven

```xml
<dependency>
    <groupId>com.ejlchina</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.ejlchina:bean-searcher:2.0.0'
```

## 集成

通常情况下，我们都是在一个后端的 Java Web 项目中使用 Bean Searcher，它可以在任意的 Web 框架中使用，以下介绍在常见的机种 Web 框架的集成方法：

### Spring Boot

由于 Bean Searcher 自 v2.0 起就已实现了 Spring Boot Starter 化，所以在 Spring Boot Web 项目中集成 Bean Searcher 最为简单，只需要在应用的配置文件内配置 Search Bean 实体类的包名路径即可，例如在 `src/main/resources/application.properties` 文件中：

```properties
bean-searcher.packages = com.example.sbean
```

其中 `com.example.sbean` 为 Search Bean 实体类所在的包名路径，可配多个。


### Spring MVC

在传统的 Spring MVC 项目中集成 Bean Searcher 需要在项目的 xml 文件内配置以下两个 Bean：

```xml
<bean id="searchSqlExecutor" 
        class="com.ejlchina.searcher.implement.MainSearchSqlExecutor" 
        p:dataSource-ref="dataSource" />

<bean id="searcher" 
        class="com.ejlchina.searcher.support.spring.SpringSearcher"
        p:searchSqlExecutor-ref="searchSqlExecutor"
        p:scanPackages="{'com.example.sbean'}" />
```

其中`com.example.sbean`为 Search Bean 实体类的包名路径，可配多个。

### Grails

在 Grails 项目中集成 Bean Searcher，只需要在 `grails-app/conf/spring/resources.groovy` 文件内配置一个 Bean 即可：

```groovy
searcher(SpringSearcher) {
    scanPackages = [
        'com.example.sbean'
    ]
    searchSqlExecutor = { MainSearchSqlExecutor e ->
        dataSource = ref('dataSource')
    }
}
```

其中`com.example.sbean`为 Search Bean 实体类的包名路径，可配多个。

### Jfinal

在 Jfinal 项目中集成 Bean Searcher，需要在配置插件的地方配置 SearchPlugin，例如：

```java
public class App extends JFinalConfig implements SearcherReceiver, SearcherConfiger {

    // 省略 Jfinal 的其它配置

    @Override
    public void configPlugin(Plugins me) {
        // 首先获得一个 IDataSourceProvider 实例，比如 DruidPlugin 插件
        DruidPlugin dp = new DruidPlugin(...);
        // 省略 DruidPlugin 相关的配置
        
        // Bean Searcher 插件，第一个参数接收 IDataSourceProvider 实例
        // 第二个参数为 Search Bean 实体类的包名路径，可配多个
        SearchPlugin sp = new SearchPlugin(dp, "com.example.sbean");
        sp.setSearcherReceiver(this);
        sp.setSearcherConfiger(this);
        sp.setShowSql(true);
        
        me.add(dp);
        me.add(sp);

        // 省略其它配置
    }

    @Override
    public void receive(Searcher searcher) {
        // 接收到 searcher 实例，可以用一个容器接收，留待后续使用
        Ioc.add(Searcher.class, searcher);
    }
    
    @Override
    public void config(SearcherBuilder builder) {
        // 这里可以对 Bean Searcher 做一些自定义配置

    }

    // 省略 Jfinal 的其它配置

}
```

其中`com.example.sbean`为 Search Bean 实体类的包名路径，可配置多个。

### Others

在其它任意的 Java 项目中，都可以使用如下方法使用 Bean Searcher : 

#### 1、启动 Bean Searcher

```java
SearcherStarter starter = new SearcherStarter();
// 传入 Search Bean 实体类的包名（可传多个），并启动
starter.start("com.example.sbean");
```

#### 2、构建 Searcher 实例

```java
// 获取配置好的数据源
DataSource dataSource = getDataSource();
// 构建 Searcher 实例
Searcher searcher = SearcherBuilder.builder()
        .configSearchSqlExecutor(new MainSearchSqlExecutor(dataSource))
        .build();
```

得到 `Searcher` 实例后，便可以在项目中使用它了。

#### 3、停止 Bean Searcher

当项目停止时，可调用 `SearcherStarter` 对象的 `shutdown` 方法优雅关闭：

```java
// 关闭检索器
starter.shutdown();
```

## 使用

当在项目中成功集成后，剩下的工作便是定义 `SearchBean` 实体类了。

### SearchBean

所谓的 `SearchBean`，即是列表检索中单个列表项所组成的 `POJO`。它指定了列表中的某个数据列与数据库表字段之间的映射关系。例如：

```java
@SearchBean(
    tables = "employee e, department d",    // 关联 员工表 与 部门表
    joinCond = "e.department_id = d.id"     // 静态关联条件
)
public class Employee {

    @DbField("e.id")                        // 关联 员工表 的 ID 字段
    private Long id;                

    @DbField("e.name")                      // 关联 员工表 的 NAME 字段
    private String name;
    
    @DbField("e.age")                       // 关联 员工表 的 AGE 字段
    private Integer age;

    @DbField("d.name")                      // 关联 部门表 的 ID 字段
    private String department;

    @DbField("e.entry_date")                // 关联 员工表的 ENTRY_DATE 字段
    private Date entryDate;

    // Getter and Setter ...
}

```



文档完善中 。。。