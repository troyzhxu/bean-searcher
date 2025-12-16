# Installation

Bean Searcher has been built based on **JDK 17** by default since `v4.5.0`. If you need compatibility with **JDK 8**, you can use the `v4.x.x.jdk8` versions.

## Gradle

To build a project using Gradle, add the following dependencies:

### JDK 21+

::: code-group
```groovy [SpringBoot 4.x]
// Use this dependency directly to utilize Bean Searcher's data retrieval functionality
implementation 'cn.zhxu:bean-searcher-boot-starter:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
```groovy [Grails 7.x]
// Use this dependency directly to utilize Bean Searcher's data retrieval functionality
implementation 'cn.zhxu:bean-searcher-boot-starter:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
```groovy [Solon]
// Solon projects can use this dependency directly (same functionality as bean-searcher-boot-starter)
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
```groovy [Others]
// Bean Searcher core dependency, any framework can use this dependency, requires manual configuration
implementation 'cn.zhxu:bean-searcher:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality, requires manual configuration
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel), requires manual configuration -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
:::

### JDK 17+

::: code-group
```groovy [SpringBoot 3.x]
// Use this dependency directly to utilize Bean Searcher's data retrieval functionality
implementation 'cn.zhxu:bean-searcher-boot-starter:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
```groovy [Grails 7.x]
// Use this dependency directly to utilize Bean Searcher's data retrieval functionality
implementation 'cn.zhxu:bean-searcher-boot-starter:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
```groovy [Solon]
// Solon projects can use this dependency directly (same functionality as bean-searcher-boot-starter)
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
```groovy [Others]
// Bean Searcher core dependency, any framework can use this dependency, requires manual configuration
implementation 'cn.zhxu:bean-searcher:4.8.2'
// If you wish to use the field labeling (dictionary translation) functionality, requires manual configuration
implementation 'cn.zhxu:bean-searcher-label:4.8.2'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel), requires manual configuration -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2'
```
:::

### JDK 8+

::: code-group
```groovy [SpringBoot 1.4 ~ 2.x]
// Use this dependency directly to utilize Bean Searcher's data retrieval functionality
implementation 'cn.zhxu:bean-searcher-boot-starter:4.8.2.jdk8'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2.jdk8'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2.jdk8'
```
```groovy [Grails 3.x ~ 6.x]
// Use this dependency directly to utilize Bean Searcher's data retrieval functionality
implementation 'cn.zhxu:bean-searcher-boot-starter:4.8.2.jdk8'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2.jdk8'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2.jdk8'
```
```groovy [Solon]
// Solon projects can use this dependency directly (same functionality as bean-searcher-boot-starter)
implementation 'cn.zhxu:bean-searcher-solon-plugin:4.8.2.jdk8'
// If you wish to use the field labeling (dictionary translation) functionality
implementation 'cn.zhxu:bean-searcher-label:4.8.2.jdk8'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2.jdk8'
```
```groovy [Others]
// Bean Searcher core dependency, any framework can use this dependency, requires manual configuration
implementation 'cn.zhxu:bean-searcher:4.8.2.jdk8'
// If you wish to use the field labeling (dictionary translation) functionality, requires manual configuration
implementation 'cn.zhxu:bean-searcher-label:4.8.2.jdk8'
// If you wish to use the data export functionality (can serve as an alternative to EasyExcel), requires manual configuration -->
// Very lightweight, does not depend on POI, simpler to use
implementation 'cn.zhxu:bean-searcher-exporter:4.8.2.jdk8'
```
:::

## Maven

To build a project using Maven, add the following dependencies:

### JDK 21+

::: code-group
```xml [SpringBoot 4.x]
<!-- Use this dependency directly to utilize Bean Searcher's data retrieval functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
```xml [Grails 7.x]
<!-- Use this dependency directly to utilize Bean Searcher's data retrieval functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
```xml [Solon]
<!-- Solon projects can use this dependency directly (same functionality as bean-searcher-boot-starter) -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
```xml [Others]
<!-- Bean Searcher core dependency, any Java framework can use this dependency, requires manual configuration -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality, requires manual configuration -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel), requires manual configuration -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
:::

### JDK 17+

::: code-group
```xml [SpringBoot 3.x]
<!-- Use this dependency directly to utilize Bean Searcher's data retrieval functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
```xml [Grails 7.x]
<!-- Use this dependency directly to utilize Bean Searcher's data retrieval functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
```xml [Solon]
<!-- Solon projects can use this dependency directly (same functionality as bean-searcher-boot-starter) -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
```xml [Others]
<!-- Bean Searcher core dependency, any Java framework can use this dependency, requires manual configuration -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality, requires manual configuration -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel), requires manual configuration -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2</version>
</dependency>
```
:::

### JDK 8+

::: code-group
```xml [SpringBoot 1.4 ~ 2.x]
<!-- Use this dependency directly to utilize Bean Searcher's data retrieval functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
```
```xml [Grails 3.x ~ 6.x]
<!-- Solon projects can use this dependency directly (same functionality as bean-searcher-boot-starter) -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
```
```xml [Solon]
<!-- Solon projects can use this dependency directly (same functionality as bean-searcher-boot-starter) -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-solon-plugin</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel) -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
```
```xml [Others]
<!-- Bean Searcher core dependency, any Java framework can use this dependency -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the field labeling (dictionary translation) functionality, requires manual configuration -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-label</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
<!-- If you wish to use the data export functionality (can serve as an alternative to EasyExcel), requires manual configuration -->
<!-- Very lightweight, does not depend on POI, simpler to use -->
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-exporter</artifactId>
    <version>4.8.2.jdk8</version>
</dependency>
```
:::

## Compatibility

### JDK Versions

* `v1.0 ~ v4.4` supports `JDK 8+` (JDK 8 and all subsequent versions)
* `v4.5 ~ v4.8` provides default support for `JDK 17+` (JDK 17 and all subsequent versions). If you need compatibility with `JDK 8 ~ 16`, you can use the `v4.x.x.jdk8` compatibility versions.
* `v5.x` (future) will only be built based on `JDK 17` and will no longer provide compatibility versions for `JDK 8 ~ 16`.

### Web Frameworks

#### 1、SpringBoot

* The `bean-searcher-boot-starter` dependency versions `v3.0.0 ~ v3.0.4` and `v3.1.0 ~ v3.1.2` support `spring-boot [v2.0, v3.0)`
* Other versions before `v4.0` support `spring-boot [v1.4+, v3.0)`
* Versions after v4.0 support `spring-boot v1.4+ (including spring-boot v3.x and v4.x)` (wider compatibility range)

#### 2、Solon

* The `bean-searcher-solon-plugin` dependency supports `solon v2.2.1+`

#### 4、Grails

* If a Grails project uses the `bean-searcher-boot-starter` dependency, it must use versions `v3.1.4+`, `v3.2.3+`, and `v3.3.1+`.
* In other words, `bean-searcher-boot-starter` versions within the range `v3.1.3-` ∪ `v3.2.0 ~ v3.2.2` ∪ `v3.3.0` do not support Grails projects.
