# 安装

## Maven

```xml
<!-- Bean Searcher 核心依赖，任何框架都可使用该依赖 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.3.5</version>
</dependency>
<!-- SpringBoot / Grails 的项目直接使用以下依赖，更为方便（只添加这一个依赖即可） -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.3.5</version>
</dependency>
<!-- Solon 项目直接使用以下依赖（只添加这一个依赖即可，功能同 bean-searcher-boot-starter） -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.3.5</version>
</dependency>
```

## Gradle

```groovy
// Bean Searcher 核心依赖，任何框架都可使用该依赖
implementation 'cn.zhxu:bean-searcher:4.3.5'
// SpringBoot / Grails 项目直接使用以下依赖，更为方便（只添加这一个依赖即可）
implementation 'cn.zhxu:bean-searcher-boot-starter:4.3.5'
// Solon 项目直接使用以下依赖（只添加这一个依赖即可，功能同 bean-searcher-boot-starter）
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.3.5'
```

::: tip 提示
* 依赖 `bean-searcher-boot-starter` 的 `v3.0.0 ~ v3.0.4` 与 `v3.1.0 ~ v3.1.2` 支持 `spring-boot [v2.0, v3.0)`。其它 `v4.0` 之前的版本支持 `spring-boot [v1.4+, v3.0)`，v4.0 之后的版本支持 `spring-boot v1.4+（包括 spring-boot v3.x）`（兼容范围更广）。
* 依赖 `bean-searcher-solon-plugin` 支持 `solon v2.2.1+` 
:::

::: warning 注意
Grails 项目若使用 `bean-searcher-boot-starter` 依赖，必须使用 `v3.1.4+`、`v3.2.3+` 与 `v3.3.1+` 的版本（即版本在 `v3.1.3-` ∪ `v3.2.0 ~ v3.2.2` ∪ `v3.3.0` 范围内的 `bean-searcher-boot-starter` 都不支持 Grails 项目）。
:::
