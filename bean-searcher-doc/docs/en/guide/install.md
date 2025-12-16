## 兼容性

### JDK 版本

* `v1.0 ~ v4.4` 支持 `JDK 8+`（JDK 8 及以后的所有版本）
* `v4.5 ~ v4.8` 默认支持 `JDK 17+`（JDK 17 及以后的所有版本），如需兼容 `JDK 8 ~ 16`，可使用 `v4.x.x.jdk8` 的兼容版本
* `v5.x`（未来）将只基于 `JDK 17` 构建，不再提供 `JDK 8 ~ 16` 的兼容版本

### Web 框架

#### 1、SpringBoot

* 依赖 `bean-searcher-boot-starter` 的 `v3.0.0 ~ v3.0.4` 与 `v3.1.0 ~ v3.1.2` 支持 `spring-boot [v2.0, v3.0)`
* 其它 `v4.0` 之前的版本支持 `spring-boot [v1.4+, v3.0)`
* v4.0 之后的版本支持 `spring-boot v1.4+（包括 spring-boot v3.x 与 v4.x）`（兼容范围更广）

#### 2、Solon

* 依赖 `bean-searcher-solon-plugin` 支持 `solon v2.2.1+`

#### 4、Grails

* Grails 项目若使用 `bean-searcher-boot-starter` 依赖，必须使用 `v3.1.4+`、`v3.2.3+` 与 `v3.3.1+` 的版本
* 即版本在 `v3.1.3-` ∪ `v3.2.0 ~ v3.2.2` ∪ `v3.3.0` 范围内的 `bean-searcher-boot-starter` 都不支持 Grails 项目。
