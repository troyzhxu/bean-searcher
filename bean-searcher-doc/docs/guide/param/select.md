# 指定 Select 字段

默认情况下 Bean Searcher 将查询 实体类里的所有映射字段，但也可使用以下参数指定需要 Select 的字段：

* `onlySelect` - 指定需要 Select 的字段
* `selectExclude` - 指定不需要 Select 的字段

## 用法

* 前端传参形式

参考：[起步 > 使用 > 开始检索](/guide/latest/start.html#（4）指定（排除）字段（onlyselect-selectexclude）) 章节。

* 参数构建器形式

```java
Map<String, Object> params = MapUtils.builder()
        .onlySelect(User::getId, User::getName)     // （1）只查询 id 与 name 字段
        .onlySelect("id", "name")                   // 等效写法 =（1）
        .onlySelect("id,name")                      // 等效写法 =（1）（since v3.7.1）
        .selectExclude(User::getAge, User::getDate) // （2）不查询 age 与 date 字段
        .selectExclude("age", "date")               // 等效写法 =（2）
        .selectExclude("age,date")                  // 等效写法 =（2）（since v3.7.1）
        .build();
List<User> users = searcher.searchList(User.class, params);
```

## 配置项

SpringBoot / Grails 项目中，可通过以下方式定制参数名：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.only-select` | onlySelect 参数名 | `字符串` | `onlySelect`
`bean-searcher.params.select-exclude` | selectExclude 参数名 | `字符串` | `selectExclude`
