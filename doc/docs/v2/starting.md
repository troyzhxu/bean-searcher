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

通常情况下，我们都是在一个后端的 Java Web 项目中使用 Bean Searcher，它可以在任意的 Web 框架中使用，以下介绍与三种 Web 框架的集成方法：

### Spring Boot

由于 Bean Searcher 自 v2.0 起就已实现了 Spring Boot Starter 化，所以在 Spring Boot Web 项目中集成 Bean Searcher 最为简单。

#### 第一步：添加依赖

详见 [安装](/v2/starting.html#安装) 章节。

#### 第二步：配置 Search Bean 实体类的包名路径

在 `src/main/resources/application.properties` 文件中：

```properties
bean-searcher.packages = com.example.sbean
```

或 `src/main/resources/application.yml` 文件中：

```properties
bean-searcher:
  packages: com.example.sbean
```

其中`com.example.sbean`为 Search Bean 实体类的包名路径，可配置多个。

以上两步便完成了在 Spring Boot 框架下的集成。

### Grails

在 Grails 项目中集成 Bean Searcher 同样只需要两步：

#### 第一步：添加依赖

详见 [安装](/v2/starting.html#安装) 章节。

#### 第二步：配置 SpringSearcher

在 `grails-app/conf/spring/resources.groovy` 文件内配置一个 Bean

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

其中`com.example.sbean`为 Search Bean 实体类的包名路径，可配置多个。

以上两步便完成了在 Grails 框架下的集成。

### Jfinal

在 Jfinal 项目中集成 Bean Searcher 同样只需要两步：

#### 第一步：添加依赖

详见 [安装](/v2/starting.html#安装) 章节。

#### 第二步：配置 SearchPlugin

在配置插件的地方配置 SearchPlugin，例如：

```java
public class App extends JFinalConfig implements SearcherReceiver, SearcherConfiger {

	// 省略 Jfinal 的其它配置

	@Override
	public void configPlugin(Plugins me) {
		loadPropertyFile("application.properties");
		// 数据库连接池插件 DruidPlugin 实现了 IDataSourceProvider 接口
		DruidPlugin dp = new DruidPlugin(getProperty("db.url"), 
				getProperty("db.username"), 
				getProperty("db.password"),
				getProperty("db.dirverClass"));
		dp.setMaxActive(getPropertyToInt("db.maxActive", 50));
		dp.setMinIdle(getPropertyToInt("db.minIdle", 10));
		
        // Bean Searcher 插件，第一个参数接收 IDataSourceProvider 实例
        // 第二个参数为 Search Bean 实体类的包名路径，可配多个
        SearchPlugin sp = new SearchPlugin(dp, "com.example.sbean");
        sp.setSearcherReceiver(this);
        sp.setSearcherConfiger(this);
        sp.setShowSql(true);
        
		me.add(dp);
		me.add(sp);
	}

	@Override
	public void receive(Searcher searcher) {
        // 接收到 searcher 实例，可以用一个容器接收，留待使用
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

以上两步便完成了在 Jfinal 框架下的集成。
