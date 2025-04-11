# 逻辑分组

> since v3.5

在默认情况下，如果检索时带有 **多个** 字段参数，则这些参数之间都是 **并且** 的关系。那如何来表达 **或者**，以及 **且或** 之间更复杂的 **逻辑组合** 呢？

![](/group_requirement.png)

终于，在 `v3.5.0` 里，Bean Searcher 为大家带来了 逻辑分组 的功能，它的主要思想如下：

* 用 **组名** 以 **前缀** 的形式为 字段参数 分组（组名与字段参数之间默认用 `.` 作为分割符，组名可由字母与数字组成）
* 用一个新的参数 **逻辑表达式** 来表示 各组之间的 逻辑关系（默认的参数名为 `gexpr`，是 `Group Expression` 的简写）

## 用法举例

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

### 写成 URL 参数形式

```txt
?A.name=Jack&A.name-ic=true&A.gender=Male&B.name=Alice&B.gender=Female&C.age=20&C.age-op=ge&gexpr=(A%7CB)%26C
```

::: tip 注意
由于 `&` 与 `|` 是特殊字符，所以在 RUL 中，参数 `gexpr` 的值需要 **URLEncode** 编码一下。
:::

### 使用 参数构建器

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

自 `v4.3.0` 起，参数构建器添加了 `and(..)` 与 `or(..)` 方法，可以更加便捷的构建逻辑分组，所以上面的代码也等价于：

```java
Map<String, Object> params = MapUtils.builder()
        .or(o -> o
            .and(a -> a
                .field(User::getName, "Jack").ic()
                .field(User::getGender, "Male")
            )
            .and(a -> a
                .field(User::getName, "Alice")
                .field(User::getGender, "Female")
            )
        )
        .field(User::getAge, "20").op(GreateEqual.class)
        .build();
// 无需再调用 groupExpr(..) 方法，and(..) 与 or(..) 方法将自动生成组名与组表达式
```

::: tip 提示
只有 [字段参数](/guide/param/field) 与 [自定义 SQL 条件](/guide/param/sql) 才可以分组，[分页参数](/guide/param/page)、[排序参数](/guide/param/sort)、[内嵌参数](/guide/param/embed) 与 [Select 参数](/guide/param/select) 都是不能被分组的。
:::

## 逻辑表达式

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
组表达式不能包含 `$` 符，因为这是框架内置的一个组（跟组），如果表达式中包含了 `$` 符，将视作无效从而被忽略。参阅 [根参数](#根参数-since-v3-8-0) 章节。
:::

### 逻辑优先级

* 逻辑符 **且** 的优先级 大于 **或** 的优先级

例如: `A | B & C` 与 `A | (B & C)` 等价。

### 智能化简

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

### 表达式合并（since v4.3.0）

有时候前端传了组表达式参数，但后端也使用了 `groupExpr(..)` 方法指定了新的表达式，这时候应该使用哪一个表达式呢？在 `v4.3.0` 之前，后端指定的表达式会覆盖前端指定的表达式。

自 `v4.3.0` 起，用户可以选择是否 **覆盖** 或者 **合并**（v4.3.0 后的默认行为，见 [配置项](#配置项) 的 `bean-searcher.params.group.mergeable` 配置）这两个表达式。

#### 合并规则

以 **并且** 关系进行合并。例如前端传表达式 `A|B`，后端用 `groupExpr(..)` 方法指定表达式 `A|C`，则最终的表达式为 `(A|B)&(A|C)`。

## 根参数（since v3.8.0）

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

## 配置项

使用 `bean-searcher-boot-starter` 依赖时，我们可以使用它提供了以下的配置键 来对进行自定义：

配置键名 | 含义 | 可选值 | 默认值
-|-|-|-
`bean-searcher.params.group.enable` | 是否开启逻辑分组功能 | `布尔` | `true`
`bean-searcher.params.group.expr-name` | 逻辑表达式参数名 | `字符串` | `gexpr`
`bean-searcher.params.group.max-expr-length ` | 组表达式最大长度 | `整型` | `50`
`bean-searcher.params.group.cache-size` | 表达式解析缓存（个数） | `整型` | `50`
`bean-searcher.params.group.separator` | 组名分隔符 | `字符串` | `.`
`bean-searcher.params.group.mergeable` | 组表达式是否可合并 | `布尔` | `true`
