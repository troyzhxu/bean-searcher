# Installation

## Maven

::: code-group
```xml [SpringBoot]
<!-- Use the following dependency directly for SpringBoot/Grails projects -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.6.0.alpha</version>
</dependency>
```
```xml [Grails]
<!-- Use the following dependency directly for SpringBoot/Grails projects -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.6.0.alpha</version>
</dependency>
```
```xml [Solon]
<!-- Use the following dependency directly for Solon projects (equivalent to bean-searcher-boot-starter) -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.6.0.alpha</version>
</dependency>
```
```xml [Others]
<!-- Core dependency of Bean Searcher, compatible with any Java framework -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.6.0.alpha</version>
</dependency>
```
:::

## Gradle

::: code-group
```groovy [SpringBoot]
// Use the following dependency directly for SpringBoot/Grails projects
implementation 'cn.zhxu:bean-searcher-boot-starter:4.6.0.alpha'
```
```groovy [Grails]
// Use the following dependency directly for SpringBoot/Grails projects
implementation 'cn.zhxu:bean-searcher-boot-starter:4.6.0.alpha'
```
```groovy [Solon]
// Use the following dependency directly for Solon projects (equivalent to bean-searcher-boot-starter)
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.6.0.alpha'
```
```groovy [Others]
// Core dependency of Bean Searcher, compatible with any framework
implementation 'cn.zhxu:bean-searcher:4.6.0.alpha'
```
:::

::: tip 提示
* Versions `v3.0.0 ~ v3.0.4` and `v3.1.0 ~ v3.1.2` of `bean-searcher-boot-starter` support `spring-boot [v2.0, v3.0)`. Other versions before `v4.0` support `spring-boot [v1.4+, v3.0)`. Versions after `v4.0` support `spring-boot v1.4+` (including `spring-boot v3.x`) with broader compatibility.  
* The `bean-searcher-solon-plugin` dependency supports `solon v2.2.1+`.  
:::

::: warning 注意
For Grails projects using the `bean-searcher-boot-starter` dependency, you **must** use versions `v3.1.4+`, `v3.2.3+`, or `v3.3.1+` (i.e., versions in the ranges `v3.1.3-`, `v3.2.0 ~ v3.2.2`, or `v3.3.0` are **not** supported for Grails).  
:::
