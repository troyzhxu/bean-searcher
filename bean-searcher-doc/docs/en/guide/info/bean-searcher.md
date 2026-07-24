
# Bean Searcher

Bean Searcher is a lightweight database conditional retrieval engine. Its function is to retrieve data from existing database tables. Its purpose is to reduce the development of backend template code, greatly improve development efficiency, and save development time, making it possible to complete a list query interface with just one line of code!

* It does not depend on a specific Web framework (i.e., it can be used in any Java Web framework).
* It does not depend on a specific ORM framework (i.e., it can be used in conjunction with any ORM framework and can also be used independently without an ORM).

## Framework Introduction

### Architecture Design Diagram

![](/architecture.jpg)

### Differences from Hibernate and MyBatis

First of all, Bean Searcher is not a complete `ORM` framework. Its purpose is not to replace them but to make up for their deficiencies in the `list retrieval field`.

The following table lists the specific differences between them:

Difference Point | Bean Searcher | Hibernate | MyBatis
-|-|-|-
ORM | Read-only ORM | Fully automatic ORM | Semi-automatic ORM
Entity classes can be mapped to multiple tables | Supported | Not supported | Not supported
Field operators | **Dynamic** | Static | Static
CRUD | Only R | CRUD | CRUD

As can be seen from the above table, Bean Searcher can only perform database queries and does not support create, update, and delete operations. However, its **multi-table mapping mechanism** and **dynamic field operators** can make our code **ten times more efficient**, or even **a hundred times more efficient** when performing complex list retrievals.

More importantly, it has no third-party dependencies and can be used in conjunction with **any ORM** in the project.

### Which projects can use it

* Java projects (of course, Kotlin and Gradle projects are also acceptable);
* Projects that use relational databases (e.g., MySQL, Oracle, etc.);
* It can be integrated with any framework: Spring Boot, Grails, JFinal, etc.

### When to use it

Every framework has its own usage scenarios. Of course, Bean Searcher is no exception. Its emergence is not to replace traditional ORMs such as MyBatis or Hibernate. Therefore, it is very important to understand which scenarios are suitable for using it.

* **It is recommended** to use it in **non-transactional** and **dynamic** retrieval scenarios. For example:
  In the retrieval scenarios of pages such as [Order Management] and [User Management] in the management background, the retrieval is **non-transactional** and does not insert data into the database. Moreover, the retrieval conditions are **dynamic**. Different user retrieval methods result in different executed SQL statements (e.g., retrieving by `order number` and retrieving by `status` require different SQL statements). In this case, it is recommended to use Bean Searcher for retrieval.
* **It is not recommended** to use it in **transactional** and **static** query scenarios. For example:
  In the user registration interface, where it is necessary to first query whether an account already exists, the interface is **transactional** as it needs to insert data into the database. At this time, the query conditions are **static**. Regardless of which account, the same SQL statement is executed (querying by `account name`). In this case, it is not recommended to use Bean Searcher for the query.

### Which databases are supported

As long as a database supports normal SQL syntax, it is supported. In addition, Bean Searcher has five built-in dialect implementations:

* Databases with the same pagination syntax as MySQL are supported by default.
* For databases with the same pagination syntax as PostgreSQL, select the PostgreSQL dialect.
* For databases with the same pagination syntax as Oracle, select the Oracle dialect.
* For databases with the same pagination syntax as SqlServer (v2012+), select the SqlServer dialect.
* For databases with the same pagination syntax as DaMeng, select the DaMeng dialect (**since v4.6.0**).

If a database has a unique pagination syntax, you only need to customize a dialect by implementing two methods. Refer to the [Advanced > SQL Dialect](/en/guide/advance/dialect) section.

## DEMO Quick Experience

> 🚀 **Try it online**: [https://demo-bs.zhxu.cn/](https://demo-bs.zhxu.cn/) — no deployment needed!

Run locally in three steps:

### Step 1: Clone

::: code-group
```bash [Gitee]
git clone https://gitee.com/troyzhxu/bean-searcher.git
```
```bash [Github]
git clone https://github.com/troyzhxu/bean-searcher.git
```
:::

### Step 2: Start a Backend (choose one)

::: code-group
```bash [SpringBoot4]
# JDK 21+
cd bean-searcher/bean-searcher-demos/backend-springboot4
./gradlew bootRun
```
```bash [SpringBoot3]
# JDK 17+
cd bean-searcher/bean-searcher-demos/backend-springboot3
./gradlew bootRun
```
```bash [SpringBoot2]
# JDK 8+
cd bean-searcher/bean-searcher-demos/backend-springboot2
mvn spring-boot:run
```
```bash [Solon4]
# JDK 17+
cd bean-searcher/bean-searcher-demos/backend-solon4
# Open and run with IDEA
```
```bash [Solon3]
# JDK 8+
cd bean-searcher/bean-searcher-demos/backend-solon3
# Open and run with IDEA
```
:::

### Step 3: Start Frontend & See Result

```bash
cd bean-searcher/bean-searcher-demos/frontend-vue
npm install && npm run dev
```

Open `http://localhost:7300` to view the demo.

> This demo follows a frontend-backend separated architecture: one unified frontend + multiple backend services with fully consistent APIs. All backends use H2 in-memory database, zero configuration required.

[More DEMOs](https://github.com/troyzhxu/bean-searcher/tree/main/bean-searcher-demos)
