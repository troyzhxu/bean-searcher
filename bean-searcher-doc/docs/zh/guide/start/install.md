# 安装

Bean Searcher 自 `v4.5.0` 起默认基于 **JDK 17** 构建，若需兼容 **JDK 8**，可使用 `v4.x.x.jdk8` 版本。

## Gradle

### JDK 17+

::: code-group
```groovy [SpringBoot 3.x]
// 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能
implementation 'cn.zhxu:bean-searcher-boot-starter:4.5.1'
// 如果希望使用字段标签（字典翻译）的功能
implementation 'cn.zhxu:bean-searcher-label:4.5.1'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1'
```
```groovy [Grails 7.x]
// 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能
implementation 'cn.zhxu:bean-searcher-boot-starter:4.5.1'
// 如果希望使用字段标签（字典翻译）的功能
implementation 'cn.zhxu:bean-searcher-label:4.5.1'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1'
```
```groovy [Solon]
// Solon 项目直接使用此依赖（功能同 bean-searcher-boot-starter）
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.5.1'
// 如果希望使用字段标签（字典翻译）的功能
implementation 'cn.zhxu:bean-searcher-label:4.5.1'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1'
```
```groovy [Others]
// Bean Searcher 核心依赖，任何框架都可使用该依赖，需要手动配置
implementation 'cn.zhxu:bean-searcher:4.5.1'
// 如果希望使用字段标签（字典翻译）的功能，需要手动配置
implementation 'cn.zhxu:bean-searcher-label:4.5.1'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案），需要手动配置 -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1'
```
:::

### JDK 8+

::: code-group
```groovy [SpringBoot 1.4 ~ 2.x]
// 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能
implementation 'cn.zhxu:bean-searcher-boot-starter:4.5.1.jdk8'
// 如果希望使用字段标签（字典翻译）的功能
implementation 'cn.zhxu:bean-searcher-label:4.5.1.jdk8'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1.jdk8'
```
```groovy [Grails 3.x ~ 6.x]
// 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能
implementation 'cn.zhxu:bean-searcher-boot-starter:4.5.1.jdk8'
// 如果希望使用字段标签（字典翻译）的功能
implementation 'cn.zhxu:bean-searcher-label:4.5.1.jdk8'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1.jdk8'
```
```groovy [Solon]
// Solon 项目直接使用此依赖（功能同 bean-searcher-boot-starter）
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.5.1.jdk8'
// 如果希望使用字段标签（字典翻译）的功能
implementation 'cn.zhxu:bean-searcher-label:4.5.1.jdk8'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1.jdk8'
```
```groovy [Others]
// Bean Searcher 核心依赖，任何框架都可使用该依赖，需要手动配置
implementation 'cn.zhxu:bean-searcher:4.5.1.jdk8'
// 如果希望使用字段标签（字典翻译）的功能，需要手动配置
implementation 'cn.zhxu:bean-searcher-label:4.5.1.jdk8'
// 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案），需要手动配置 -->
// 非常轻量，不依赖 POI, 用法更简单
implementation 'cn.zhxu:bean-searcher-exporter:4.5.1.jdk8'
```
:::

## Maven

### JDK 17+

::: code-group
```xml [SpringBoot 3.x]
<!-- 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1</version>
</dependency>
```
```xml [Grails 7.x]
<!-- 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1</version>
</dependency>
```
```xml [Solon]
<!-- Solon 项目直接使用此依赖（功能同 bean-searcher-boot-starter） -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1</version>
</dependency>
```
```xml [Others]
<!-- Bean Searcher 核心依赖，任何 Java 框架都可使用该依赖，需要手动配置 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能，需要手动配置 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案），需要手动配置 -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1</version>
</dependency>
```
:::

### JDK 8+

::: code-group
```xml [SpringBoot 1.4 ~ 2.x]
<!-- 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
```
```xml [Grails 3.x ~ 6.x]
<!-- 直接使用此依赖，即可使用 Bean Searcher 的数据检索功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
```
```xml [Solon]
<!-- Solon 项目直接使用此依赖（功能同 bean-searcher-boot-starter） -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案） -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
```
```xml [Others]
<!-- Bean Searcher 核心依赖，任何 Java 框架都可使用该依赖 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用字段标签（字典翻译）的功能，需要手动配置 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
<!-- 如果希望使用数据导出的功能（可作为 EasyExcel 的替代方案），需要手动配置 -->
<!-- 非常轻量，不依赖 POI, 用法更简单 -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.5.1.jdk8</version>
</dependency>
```
:::

## 兼容性

::: tip 提示
* 依赖 `bean-searcher-boot-starter` 的 `v3.0.0 ~ v3.0.4` 与 `v3.1.0 ~ v3.1.2` 支持 `spring-boot [v2.0, v3.0)`。其它 `v4.0` 之前的版本支持 `spring-boot [v1.4+, v3.0)`，v4.0 之后的版本支持 `spring-boot v1.4+（包括 spring-boot v3.x）`（兼容范围更广）。
* 依赖 `bean-searcher-solon-plugin` 支持 `solon v2.2.1+` 
:::

::: warning 注意
Grails 项目若使用 `bean-searcher-boot-starter` 依赖，必须使用 `v3.1.4+`、`v3.2.3+` 与 `v3.3.1+` 的版本（即版本在 `v3.1.3-` ∪ `v3.2.0 ~ v3.2.2` ∪ `v3.3.0` 范围内的 `bean-searcher-boot-starter` 都不支持 Grails 项目）。
:::
