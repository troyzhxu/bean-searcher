# 安装

## Maven

::: code-group
```xml [SpringBoot]
<!-- SpringBoot / Grails 的项目直接使用以下依赖 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.3.6</version>
</dependency>
```
```xml [Grails]
<!-- SpringBoot / Grails 的项目直接使用以下依赖 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.3.6</version>
</dependency>
```
```xml [Solon]
<!-- Solon 项目直接使用以下依赖（功能同 bean-searcher-boot-starter） -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.3.6</version>
</dependency>
```
```xml [Others]
<!-- Bean Searcher 核心依赖，任何框架都可使用该依赖 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.3.6</version>
</dependency>
```
:::

## Gradle

::: code-group
```groovy [SpringBoot]
// SpringBoot / Grails 项目直接使用以下依赖
implementation 'cn.zhxu:bean-searcher-boot-starter:4.3.6'
```
```groovy [Grails]
// SpringBoot / Grails 项目直接使用以下依赖
implementation 'cn.zhxu:bean-searcher-boot-starter:4.3.6'
```
```groovy [Solon]
// Solon 项目直接使用以下依赖（功能同 bean-searcher-boot-starter）
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.3.6'
```
```groovy [Others]
// Bean Searcher 核心依赖，任何框架都可使用该依赖
implementation 'cn.zhxu:bean-searcher:4.3.6'
```
:::

::: tip 提示
* 依赖 `bean-searcher-boot-starter` 的 `v3.0.0 ~ v3.0.4` 与 `v3.1.0 ~ v3.1.2` 支持 `spring-boot [v2.0, v3.0)`。其它 `v4.0` 之前的版本支持 `spring-boot [v1.4+, v3.0)`，v4.0 之后的版本支持 `spring-boot v1.4+（包括 spring-boot v3.x）`（兼容范围更广）。
* 依赖 `bean-searcher-solon-plugin` 支持 `solon v2.2.1+` 
:::

::: warning 注意
Grails 项目若使用 `bean-searcher-boot-starter` 依赖，必须使用 `v3.1.4+`、`v3.2.3+` 与 `v3.3.1+` 的版本（即版本在 `v3.1.3-` ∪ `v3.2.0 ~ v3.2.2` ∪ `v3.3.0` 范围内的 `bean-searcher-boot-starter` 都不支持 Grails 项目）。
:::
