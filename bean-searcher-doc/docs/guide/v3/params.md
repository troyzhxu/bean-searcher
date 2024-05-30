# 检索参数

检索参数是 Bean Searcher 的重要检索信息，它们共同组成了 [`Searcher`](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/com/troyzhxu/searcher/Searcher.java) 接口的检索方法的 第二个 类型为 `Map<String, Object>` 的参数值。

::: tip 重要提示
Bean Searcher 的检索 **参数** 与 **数据库表字段** 是 **解耦** 的，下文所说的 **字段**，均是指 **实体类** 的字段，即实体类的 **属性**。
:::

> 如果您还没有阅读 [介绍 > 为什么用](/guide/v3/introduction.html#为什么用) 章节，建议先阅读它们。

## 分页参数

Bean Searcher 提供了两种分页：**Page 分页** 与 **Offset 分页**。

### 可配置项

在 SpringBoot / Grails 项目中，若使用了 `bean-searcher-boot-starter` 依赖，则可在项目配置文件 `application.properties` 或 `application.yml` 中对分页进行个性化配置：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.pagination.type` | 分页类型 | `page`、`offset` | `page`
`bean-searcher.params.pagination.default-size` | 默认每页查询条数 | `正整数` | `15`
`bean-searcher.params.pagination.max-allowed-size` | 每页最大查询条数（分页保护） | `正整数` | `100`
`bean-searcher.params.pagination.page` | 页码参数名（在 type = page 时有效） | `字符串` | `page`
`bean-searcher.params.pagination.size` | 每页大小参数名 | `字符串` | `size`
`bean-searcher.params.pagination.offset` | 偏移参数名（在 type = offset 时有效） | `字符串` | `offset`
`bean-searcher.params.pagination.start` | 起始页码 或 起始偏移量 | `自然数` | `0`

### Page 分页

> 分页类型为 `page` 时生效

Page 分页提供两个分页参数（参数名可配置）：

* `page`: 页码
* `size`: 每页查询条数

用法示例（默认配置下）：

```java
Map<String, Object> params = MapUtils.builder()
        .page(0, 15)                    // 第 0 页，每页 15 条（推荐写法）
        .put("page", 0)                 // 等效写法
        .put("size", 15)                // 等效写法
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

### Offset 分页

> 分页类型为 `offset` 时生效

Offset 分页也提供两个分页参数（参数名可配置）：

* `offset`: 偏移量
* `size`: 每页查询条数

用法示例（默认配置下）：

```java
Map<String, Object> params = MapUtils.builder()
        .limit(0, 15)                   // 偏移 0 条，查询 15 条（推荐写法）
        .put("offset", 0)               // 等效写法
        .put("size", 15)                // 等效写法
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

### 起始 页码/偏移量

配置项 起始页码/偏移量（`bean-searcher.params.pagination.start`）默认是 `0`，在 Page 分页机制下，`page` 参数为 0 表示查询第 1 页。当把 起始页码 配置为 `1` 时，则 `page` 参数为 1 才表示查询第 1 页。Offset 分页同理。

::: warning 注意
* **v3.7.0** 以前 `参数构建工具` 的 `page(long page, int size)` 与 `limit(long offset, int size)` 方法不受该配置影响。
* **v3.7.0** 及以后版本该配置则对  `参数构建工具`  同样有作用。
:::

### 最大查询条数

配置项 最大查询条数（`bean-searcher.params.pagination.max-allowed-size`）默认是 `100`，它可以风控一些恶意查询：比如黑客想通过一次查询 **1 亿** 条数据从而让我们系统崩溃时，Bean Searcher 会自动把它缩小为 `100`。

### 默认分页大小

配置项 默认分页大小（`bean-searcher.params.pagination.default-size`）默认是 `15`，在用户为添加分页参数时，默认每页查询 15 条数据。

::: tip
`Searcher` 实例的 `searchAll(...)` 方法不受分页参数影响
:::

## 排序参数

### 可配置项

在 SpringBoot / Grails 项目中，若使用了 `bean-searcher-boot-starter` 依赖，可在项目的 `application.properties` 或 `application.yml` 文件中通过如下配置项对排序参数进行定制：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.sort` | 排序字段参数名 | `字符串` | `sort`
`bean-searcher.params.order` | 排序方法参数名 | `字符串` | `order`
`bean-searcher.params.order-by` | 排序参数名（since v3.4.0） | `字符串` | `orderBy`

### 单字段排序

用法示例（默认配置下）：

```java
Map<String, Object> params = MapUtils.builder()
        .orderBy(User::getAge).desc()               // age 字段，降序（since v3.7.1）（推荐写法）
        .orderBy(User::getAge, "desc")              // 等效写法 1
        .put("sort", "age")                         // 等效写法 2
        .put("order", "desc")                       // 等效写法 2
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

### 多字段排序（since v3.4）

```java
Map<String, Object> params = MapUtils.builder()
        .orderBy(User::getAge).asc()                // age 字段 升序
        .orderBy(User::getTime).desc()              // time 字段 降序（多次调佣 orderBy 方法）
        .put("orderBy", "age:asc,time:desc")        // 等效写法      
        .build();
SearchResult<User> result = searcher.search(User.class, params);
```

## 字段参数

字段参数是根据 检索实体类里具有数据库字段映射的 属性 衍生出来的一系列参数，它们起到对查询结果进行 **筛选** 的作用。

### 可配置项

在 SpringBoot / Grails 项目中，若使用了 `bean-searcher-boot-starter` 依赖，可在项目的 `application.properties` 或 `application.yml` 文件中通过如下配置项对字段参数进行定制：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.separator` | 字段参数名分隔符 | `字符串` | `-`
`bean-searcher.params.operator-key` | 字段运算符参数名后缀 | `字符串` | `op`
`bean-searcher.params.ignore-case-key` | 是否忽略大小写字段参数名后缀 | `字符串` | `ic`

### 字段衍生规则

示例，对于如下的一个实体类:

```java
public class User {
    private String name;
    // 省略其它..
}
```

以它的 `name` 字段为例，可衍生出以下一系列的字段参数：

* **`name-{n}`**： name 字段的第 n 个参数值，如：`name-0`、`name-1`、`name-2` 等等（[理解不了？参考这里](/guide/v3/start.html#（11）字段过滤（-field-op-bt-）)）
* **`name`**： 等价于 `name-0`，name 字段的第 0 个参数值
* **`name-op`**： name 的 [字段运算符](/guide/v3/params.html#字段运算符)，如: `Equal`、`GreaterEqual`、`GreaterThan` 等等
* **`name-ic`**： name 字段在检索时是否应该忽略大小写

以上在衍生字段参数时，用到了中划线（`-`）作为连接符，如果你喜欢下划线（`_`），可把 `bean-searcher.params.separator` 配置为下划线即可。配置为下划线后，衍生出的参数就是 `name_{n}`、`name`、`name_op`、`name_ic` 了。同理: `op` 与 `ic` 后缀您也可以自定义。

::: tip
字段参数，是根据实体类里的 **JAVA 字段名**（不是表字段）衍生出来的，已经和数据库的表字段解耦了。
:::

### 字段运算符

字段运算符是用来描述某个字段的检索方式，即：SQL 的拼接方法。Bean Searcher 共默认提供了 19 种不同的字段运算符，见下表：

> 下表中的 `忽略空值` 的含义是：如果该字段的参数值为 `null` 或 `空串`，是否忽略该条件。

运算符 | 缩写 | SQL 片段 | 是否忽略空值 | 含义
-|-|-|-|-
`Equal` | `eq` | `x = ?` | 是 | 等于（是缺省默认的运算符）
`NotEqual` | `ne` | `x != ?` | 是 | 不等于
`GreaterThan` | `gt` | `x > ?` | 是 | 大于
`GreaterEqual` | `ge` | `x >= ?` | 是 | 大于等于
`LessThan` | `lt` | `x < ?` | 是 | 小于
`LessEqual` | `le` | `x <= ?` | 是 | 小于等于
`Between` | `bt` | `x between ?1 and ?2` / `x >= ?1` / `x <= ?2` | 是 | 在...之间（范围查询）
`NotBetween` | `nb` | `x not between ?1 and ?2` / `x < ?1` / `x > ?2` | 是 | 不在...之间（范围查询）（**since v3.3**）
`Contain` | `ct` | `x like '%?%'` | 是 | 包含（模糊查询）（**since v3.2**）
`StartWith` | `sw` | `x like '?%'` | 是 | 以...开头（模糊查询）
`EndWith` | `ew` | `x like '%?'` | 是 | 以...结尾（模糊查询）
`OrLike` | `ol` | `x like ?1 or x like ?2 or ...` | 是 | 模糊或匹配（可有多个参数值）（**since v3.7**）
`NotLike` | `nk` | `x not like ?` | 是 | 反模糊匹配（**since v3.8**）
`InList`  | `il` / `mv` | `x in (?, ?, ...)` | 是 | 多值查询（`InList` / `il` **自 v3.3 新增**，之前是 `MultiValue` / `mv`）
`NotIn` | `ni` | `x not in (?, ?, ...)` | 是 | 多值查询（**since v3.3**）
`IsNull` | `nl` | `x is null` | 否 | 为空（**since v3.3**）
`NotNull` | `nn` | `x is not null` | 否 | 不为空（**since v3.3**）
`Empty` | `ey` | `x is null or x = ''` | 否 | 为空（仅适用于 **字符串** 类型的字段）
`NotEmpty` | `ny` | `x is not null and x != ''` | 否 | 不为空（仅适用于 **字符串** 类型的字段）

::: tip 除此之外
你还可以自定义运算符，参见 [高级 > 玩转运算符](/guide/v3/advance.html#玩转运算符（since-v3-3-0）) 章节。
:::

由于 Bean Searcher 为运算符提供了全称与缩写，所以对于每一种运算符，都有几种等效的用法，

例如，查询 `name` 等于 Jack 的用户：

* 后端参数构建：

```java
Map<String, Object> params = MapUtils.builder()
        .field("name", "Jack")          // (1) 字段 name 的值为 Jack
        .field(User::getName, "Jack")   // 等效写法 == (1) 
        .op("eq")                       // (2) 指定 name 字段的运算符为 eq (默认就是 eq, 所以也可以省略)
        .op("Equal")                    // 等效写法 == (2) 
        .op(Operator.Equal)             // 等效写法 == (2) 
        .op(Equal.class)                // 等效写法 == (2) 
        .build();
User jack = searcher.searchFirst(User.class, params);           // 执行查询
```

* 前端传参形式：

```js
GET /users ? name=Jack & name-op=eq        // (1) 字段 name 的值为 Jack，运算符为 eq
GET /users ? name=Jack & name-op=Equal     // 等效写法 == (1) 
GET /users ? name-0=Jack & name-op=eq      // 等效写法 == (1) 
GET /users ? name-0=Jack & name-op=Equal   // 等效写法 == (1) 
GET /users ? name=Jack          // (2) 当 name 无运算符约束，或运算符约束中第一个为 Equal 时，与 (1) 等效 
GET /users ? name-0=Jack        // 等效写法 == (2) 
```

## 逻辑分组（since v3.5）

在默认情况下，如果检索时带有 **多个** 字段参数，则这些参数之间都是 **并且** 的关系。那如何来表达 **或者**，以及 **且或** 之间更复杂的 **逻辑组合** 呢？

![](/group_requirement.png)

### 灵光乍现

为了解决这个问题，作者昼思夜想，终于在 `v3.5.0` 里，为大家带来了 逻辑分组 的功能，它的主要思想如下：

* 用 **组名** 以 **前缀** 的形式为 字段参数 分组（组名与字段参数之间默认用 `.` 作为分割符，组名可由字母与数字组成）
* 用一个新的参数 **逻辑表达式** 来表示 各组之间的 逻辑关系（默认的参数名为 `gexpr`，是 `Group Expression` 的简写）

### 举个例子

在传递参数时，把参数分为 `A`, `B`, `C` 三组：

```properties
# A 组：姓名 Jack ，不区分大小写，并且是 男性
A.name = Jack
A.name-ic = true
A.gender = Male
# B 组：姓名 Alice，并且是 女性
B.name = Alice
B.gender = Female
# C 组：年龄 大于等于 20 岁
C.age = 20
C.age-op = ge 
```

然后再传递一个 新参数 组表达式 `gexpr` 来表示三组之间的逻辑关系：

```properties
# 组表达式：(A 或者 B) 并且 C
gexpr = (A|B)&C
```

这样就表示了一个较为复杂的查询条件：（姓名等于 Jack (不区分大小写) 的男性 或者 姓名等于 Alice 的女性 ）并且 年龄都 大于等于 20 岁

#### 写成 URL 参数形式

```txt
?A.name=Jack&A.name-ic=true&A.gender=Male&B.name=Alice&B.gender=Female&C.age=20&C.age-op=ge&gexpr=(A%7CB)%26C
```

::: tip 注意
由于 `&` 与 `|` 是特殊字符，所以在 RUL 中，参数 `gexpr` 的值需要 **URLEncode** 编码一下。
:::

#### 使用 参数构建器

我们也可以用参数构建器来表达同样的逻辑关系：

```java
Map<String, Object> params = MapUtils.builder()
        .group("A")             // A 组开始
        .field(User::getName, "Jack").ic()
        .field(User::getGender, "Male")
        .group("B")             // B 组开始
        .field(User::getName, "Alice")
        .field(User::getGender, "Female")
        .group("C")             // C 组开始
        .field(User::getAge, "20").op(GreateEqual.class)
        .groupExpr("(A|B)&C")   // 组间逻辑关系（组表达式）
        .build();
```

::: tip 提示
只有 [字段参数](/guide/v3/params.html#字段参数) 才可以分组，[分页参数](/guide/v3/params.html#分页参数)、[排序参数](/guide/v3/params.html#排序参数)、[内嵌参数](/guide/v3/params.html#内嵌参数) 与 [Select 参数](/guide/v3/params.html#指定-select-字段) 都是不能分组的。
:::

### 逻辑表达式

上文已经看到，逻辑表达式是由 **组名**、**逻辑符**（或 `|`、且 `&`）与 **小括号** 组成的 用于表示 字段参数组 之间的逻辑关系的 一个式子。

它可以很简单，也可以嵌套多层，例如：

* 为空时，表示参数不分组
* 可由单个组名构成，例如 `A` 就表示 A 组条件
* `A|B` 表示 A 或者 B
* `A&B` 表示 A 并且 B
* `(A&(B|C)|D)&F` 也是一个合法的嵌套逻辑表达式
* 组名、逻辑符 与 小括号 之间可以有空格
* 表达式内除 逻辑符、小括号 与 间隙空格 之外的部分都会被视做组名
* 当 左右小括号 不能相互匹配时，将视作 非法表达式，如：`(A&B`
* 非法表达式 在检索时 将输出警告，并会被检索忽略

::: warning 注意
组表达式不能包含 `$` 符，因为这是框架内置的一个组（跟组），如果表达式中包含了 `$` 符，将视作无效从而被忽略。参阅 [根参数](/guide/v3/params.html#根参数（since-v3-8-0）) 章节。
:::

#### 逻辑优先级

* 逻辑符 **且** 的优先级 大于 **或** 的优先级

例如: `A | B & C` 与 `A | (B & C)` 等价。

### 智能优化

Bean Searcher 还内置一个优化器，当你的逻辑表达式写的冗余复杂时，它会自动将其优化为最简形式，从而简化最终生成的 SQL 语句。

例如：

原始表达式 | 优化后
-|-
`(( A ))` | `A`
`A & A & A` | `A`
`A \| A \| A` | `A`
`A & ( A \| B )` | `A`
`A \| ( A & B )` | `A`
`A \| ( B \| C )` | `A \| B \| C`
`A & ( B & C )` | `A & B & C`
`(A \| B & (( C \| (D \| E))) & D) \| (F)` | `A \| B & D \| F`
`A \| (A \| C) & B & (A \| D)` | `A \| D & C & B`

### 根参数（since v3.8.0）

当指定了组表达式后，所有不在表达式指定组内的 **字段参数** 都会被 检索器 忽略，例如：

```properties
# A 组
A.name = Jack
# 组外字段参数，当 gexpr 非空且合法时将会被忽略
age = 20
# 组表达式
gexpr = A
```

但有时候组表达式 `gexpr` 是需要前端指定的，同时后端也需要注入一些参数，并且 **不能被忽略**，这时该怎么办呢？此时只需多注入一个根组（用 `$` 表示）参数即可：

```properties
# 组外字段参数，当 gexpr 为空或非法时有效
age = 20
# 根组参数，当 gexpr 非空且合法时有效
$.age = 20
```

组 `$` 是框架内置的一个组，它与 `gexpr` 之间永远是 `且` 的关系。

为了让某个字段参数不能被忽略，我们必须向检索参数中注入两个参数（如上面的 `age` 与 `$.age`），这略显麻烦。为此，`v3.8.0` 同时增强了参数构建器，使其 `field(..)` 方法在 **未显示指定组** 之前，都会自动添加对应的根参数。例如：

```java
Map<String, Object> params = MapUtils.builder()
        // 未显示指定组 之前调用 field 方法
        .field(User::getAge, 20) 
        // 等效于下面的两行代码：
        //   .put("age", 20) 
        //   .put("$.age", 20) 
        .group("A")
        // 显示指定组 之后调用 field 方法
        .field(User::getName, "Jack") 
        // 只等效于：
        //   .put("A.name", "Jack")
        .build()
```

所以当后端需要手动添加检索条件时，我们推荐您使用参数构建器。

### 自定义

使用 `bean-searcher-boot-starter` 依赖时，我们可以使用它提供了以下的配置键 来对进行自定义：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.group.enable` | 是否开启逻辑分组功能 | `布尔` | `true`
`bean-searcher.params.group.expr-name` | 逻辑表达式参数名 | `字符串` | `gexpr`
`bean-searcher.params.group.expr-cache-size` | 表达式解析缓存（个数） | `整型` | `50`
`bean-searcher.params.group.separator` | 组名分隔符 | `字符串` | `.`

如果你还想自定义逻辑符，比如你想用 `/ +` 来表示 或与且，则只需要注册一个 `ExprParser.Factory` 类型的 Spring Bean 即可：

```java
@Bean
public ExprParser.Factory myExprParserFactory() {
    DefaultParserFactory factory = new DefaultParserFactory();
    factory.setOrKey('/');      // 设置或逻辑符
    factory.setAndKey('+');     // 设置且逻辑符
    return factory;
}
```

## 内嵌参数

内嵌参数，即是：嵌入到实体类注解内的参数（参见：[实体类 > 嵌入参数](/guide/v3/bean.html#嵌入参数) 章节），它可分为 普通内嵌参数 与 拼接参数，他们可以轻松处理各种复杂的 SQL 检索问题。

### 普通内嵌参数

普通内嵌参数，是以一个冒号（`:`）前缀（形如 `:name`）的形式嵌入到实体类注解的 SQL 片段中的参数。

例如，有这样的一个 SearchBean：

```java
@SearchBean(where = "age = :age") 
public class Student {
    // 省略 ...
}
```

则我们可以用如下方式检索年龄为 20 的学生：

```java
Map<String, Object> params = MapUtils.builder()
        // 注意：这里不能使用 field 方法，因为 age 字段不是实体类的属性
        .put("age", 20)         // 指定内嵌参数 age 的值为 20  
        .build();
List<User> users = searcher.searchList(User.class, params);
```

::: tip
普通内嵌参数 最终会被 Bean Searcher 处理为一种 JDBC 参数，无需担心 SQL 注入问题。
:::

### 拼接参数

拼接参数（since v2.1），是以一个冒号（`:`）为前缀一个冒号（`:`）为后缀（形如 `:name:`）的形式嵌入到实体类注解的 SQL 片段中的参数。

拼接参数的用武之地非常广：能用 普通内嵌参数 的地方肯定能用 拼接参数，而 普通内嵌参数 搞不定的地方 拼接参数 则可以轻松搞定，它可以做到 **动态生成 SQL**。

::: tip 特别注意
拼接参数会直接拼接在 SQL 内，开发者在检索时应 **先检查该参数值的合法性，以免产生 SQL 注入漏洞**。如果某个需求用 普通内嵌参数 和 拼接参数 都可以解决，我们推荐您使用 普通内嵌参数 去实现它。
::: 

* 参考：[实体类 > 嵌入参数](/guide/v3/bean.html#嵌入参数) 章节；
* 参考：[示例 > 动态检索 > 分表检索](/guide/v3/simples.html#分表检索) 案例。

## 指定 Select 字段

默认情况下 Bean Searcher 将查询 实体类里的所有映射字段，但也可使用以下参数指定需要 Select 的字段：

* `onlySelect` - 指定需要 Select 的字段
* `selectExclude` - 指定不需要 Select 的字段

### 用法

* 前端传参形式

参考：[起步 > 使用 > 开始检索](/guide/v3/start.html#（4）指定（排除）字段（onlyselect-selectexclude）) 章节。

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

### 配置项

SpringBoot / Grails 项目中，可通过以下方式定制参数名：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.only-select` | onlySelect 参数名 | `字符串` | `onlySelect`
`bean-searcher.params.select-exclude` | selectExclude 参数名 | `字符串` | `selectExclude`

## 自定义条件（since v3.8）

为了保障系统的安全性，自定义 SQL 条件只允许后端通过 参数构建器的 `sql(...)` 方法来实现。

### 字段引用

在自定义的 SQL 片段中，用 `$n` 来表示所需引用的第 `n` 个字段，例如：

```java
Map<String, Object> params = MapUtils.builder()
       // 生成 SQL 条件：u.id in (select user_id from xxx)
       .field(User::getId).sql("$1 in (select user_id from xxx)")
       .build();
List<User> users = searcher.searchList(User.class, params);
```

再如：

```java
Map<String, Object> params = MapUtils.builder()
       // 生成 SQL 条件：id < 100 or age > 10
       .field(User::getId, User::getAge).sql("$1 < 100 or $2 > 10")
       .build();
List<User> users = searcher.searchList(User.class, params);
```

### SQL 参数

我们也可以在自定义的 SQL 片段中使用占位符（作为 JDBC 参数），例如：

```java
 Map<String, Object> params = MapUtils.builder()
       // 生成 SQL 条件：id < ? or age > ?，两个占位符参数分别为：100，10
       .field(User::getId, User::getAge).sql("$1 < ? or $2 > ?", 100, 10)
       .build();
List<User> users = searcher.searchList(User.class, params);
```

## 风控参数

Bean Searcher 默认提供了一些风险控制项，并支持配置。

### 可配置项

在 SpringBoot / Grails 项目中，若使用了 `bean-searcher-boot-starter` 依赖，则可在项目配置文件 `application.properties` 或 `application.yml` 中使用如下配置项：

配置键名 | 含义 | 可选值 | 默认值 | 开始版本
-|-|-|-|-
`bean-searcher.params.pagination.max-allowed-size` | 每页最大查询条数（分页保护） | `正整数` | `100` | v2.0.0
`bean-searcher.params.pagination.max-allowed-offset` | 最大分页深度 | `正整数` | `20000` | v3.8.1
`bean-searcher.params.filter.max-para-map-size` | 检索参数最大允许的键值对数 | `正整数` | `150` | v3.8.1
`bean-searcher.params.group.max-expr-length` | 逻辑分组表达式的最大长度（字符数） | `正整数` | `50` | v3.8.1

## 参数配置

上文各参数的配置均以使用 `bean-searcher-boot-starter` 依赖的 SpringBoot / Grails 项目为例，本章节介绍在以上各参数在只使用 `bean-searcher` 依赖的其它框架中的配置方法。

### 非 Boot 的 Spring 项目

```xml
<!-- page 分页，与 offset 分页 配置一个即可 -->
<bean id="pageSizeExtractor" class="com.ejlchina.searcher.implement.PageSizeExtractor">
    <property name="sizeName" value="size">                   <!-- 每页大小参数名 -->
    <property name="pageName" value="page">                   <!-- 页码参数名 -->
    <property name="start" value="0">                         <!-- 起始页码 -->
    <property name="maxAllowedSize" value="100">              <!-- 允许的最大页大小 -->
    <property name="defaultSize" value="15">                  <!-- 默认分页大小 -->
</bean>
<!-- offset 分页，与 page 分页 配置一个即可 -->
<bean id="pageOffsetExtractor" class="com.ejlchina.searcher.implement.PageOffsetExtractor">
    <property name="sizeName" value="size">                   <!-- 每页大小参数名 -->
    <property name="offsetName" value="offset">               <!-- 偏移条数参数名 -->
    <property name="start" value="0">                         <!-- 起始页码 -->
    <property name="maxAllowedSize" value="100">              <!-- 允许的最大页大小 -->
    <property name="defaultSize" value="15">                  <!-- 默认分页大小 -->
</bean>
<bean id="paramResolver" class="com.ejlchina.searcher.implement.DefaultParamResolver">
    <property name="pageExtractor" ref="pageSizeExtractor">   <!-- 分页方式：pageSizeExtractor / pageOffsetExtractor -->
    <property name="sortName" value="sort">                   <!-- 排序字段参数名 -->
    <property name="orderName" value="order">                 <!-- 排序方法参数名 -->
    <property name="separator" value="-">                     <!-- 参数名分割符 -->
    <property name="operatorSuffix" value="op">               <!-- 字段运算符参数名后缀 -->
    <property name="ignoreCaseSuffix" value="ic">             <!-- 忽略大小写参数名后缀 -->
    <property name="onlySelectName" value="onlySelect">       <!-- onlySelect 参数名 -->
    <property name="selectExcludeName" value="selectExclude"> <!-- selectExclude 参数名 -->
</bean>
<bean id="mapSearcher" class="com.ejlchina.searcher.implement.DefaultMapSearcher">
    <!-- 省略其它属性配置，BeanSearcher 检索器也同此配置 -->
    <property name="paramResolver" ref="paramResolver">
</bean>
```

### Others

```java
// page 分页，与 offset 分页 配置一个即可
PageSizeExtractor pageSizeExtractor = new PageSizeExtractor();
pageSizeExtractor.setSizeName("size");          // 每页大小参数名
pageSizeExtractor.setPageName("page");          // 页码参数名
pageSizeExtractor.setStart(0);                  // 起始页码
pageSizeExtractor.setMaxAllowedSize(100);       // 允许的最大页大小
pageSizeExtractor.setDefaultSize(15);           // 默认分页大小
// offset 分页，与 page 分页 配置一个即可
PageOffsetExtractor pageOffsetExtractor = new PageOffsetExtractor();
pageOffsetExtractor.setSizeName("size");        // 每页大小参数名
pageOffsetExtractor.setOffsetName("offset");    // 偏移条数参数名
pageOffsetExtractor.setStart(0);                // 起始页码
pageOffsetExtractor.setMaxAllowedSize(100);     // 允许的最大页大小
pageOffsetExtractor.setDefaultSize(15);         // 默认分页大小
// 参数解析器
DefaultParamResolver paramResolver = new DefaultParamResolver();
paramResolver.setPageExtractor(pageSizeExtractor);      // 分页方式：pageSizeExtractor / pageOffsetExtractor
paramResolver.setSortName("sort");                      // 排序字段参数名
paramResolver.setOrderName("order");                    // 排序方法参数名
paramResolver.setSeparator("-");                        // 参数名分割符
paramResolver.setOperatorSuffix("op");                  // 字段运算符参数名后缀
paramResolver.setIgnoreCaseSuffix("ic");                // 忽略大小写参数名后缀
paramResolver.setOnlySelectName("onlySelect");          // onlySelect 参数名
paramResolver.setSelectExcludeName("selectExclude");    // selectExclude 参数名
// 构建 MapSearcher 检索器
MapSearcher mapSearcher = SearcherBuilder.mapSearcher()
        // 省略其它配置，BeanSearcher 检索器也同此配置
        .paramResolver(paramResolver)
        .build();
```
