---
title: Bean Searcher Overview | Java Declarative Search Framework
description: Bean Searcher is a Java declarative search framework — entities define search boundaries, parameters drive query logic. Zero-annotation search for single tables. Supports Spring Boot / Solon.
head:
  - - meta
    - property: og:title
      content: Bean Searcher - Java Declarative Search Framework
  - - meta
    - property: og:description
      content: Entities define search boundaries, parameters drive query logic. Zero annotations needed for single tables.
---

# Bean Searcher

Bean Searcher is a **Java declarative search framework** — entities define search boundaries, parameters drive query logic. Single-table entities are searchable with zero annotations. One line of code handles pagination, filtering, sorting, stats, and multi-table joins.

* It does not depend on a specific Web framework (i.e., it can be used in any Java Web framework).
* It does not depend on a specific ORM framework (i.e., it can be used in conjunction with any ORM framework and can also be used independently without an ORM).

## Architecture Design Diagram

![](/architecture.jpg)

## Understanding in One Sentence: The GraphQL of List Retrieval

If you know GraphQL, understanding Bean Searcher takes one sentence:

> **GraphQL lets clients freely specify which fields to return in one request; Bean Searcher lets clients freely specify which fields to return, which conditions to filter by, and which fields to sort by — all in one request.**

The difference: GraphQL requires a dedicated protocol and schema; Bean Searcher works on standard HTTP parameters — GET, POST, form submissions, protocol-agnostic, zero migration cost.

| | GraphQL | Bean Searcher |
|-|---------|---------------|
| Domain | API data queries | Database list retrieval |
| Client control | Specifies return fields | Specifies return fields + filter conditions + sort rules + pagination |
| Protocol | POST + GraphQL body | Standard HTTP parameters (any method) |
| Core idea | Declare what you want | Entities define boundaries, parameters drive queries |
| Adoption cost | Requires API layer changes | Add one dependency |

Just as GraphQL replaces multiple REST calls with one query, Bean Searcher replaces layers of if-else condition stitching with one `search()` call.

## Relationship with MyBatis / Hibernate

First of all, Bean Searcher is not an ORM framework. Its purpose is not to replace MyBatis or Hibernate but to fill the gap they leave in the `list retrieval field` — just as GraphQL isn't meant to replace REST, but to offer greater flexibility in query scenarios.

The following table lists the specific differences between them:

Difference Point | Bean Searcher | Hibernate | MyBatis
-|-|-|-
Positioning | Declarative Search Framework | Fully automatic ORM | Semi-automatic ORM
Entity classes can be mapped to multiple tables | Supported | Not supported | Not supported
Field operators | **Dynamic (client-driven)** | Static | Static
CRUD | Read-only (R) | CRUD | CRUD
Relationship with ORM | Complementary coexistence | — | —

As can be seen from the above table, Bean Searcher can only perform database queries and does not support create, update, and delete operations. However, its **multi-table mapping mechanism** and **dynamic field operators** can make our code **ten times more efficient**, or even **a hundred times more efficient** when performing complex list retrievals.

More importantly, it has no third-party dependencies and can be used in conjunction with **any ORM** in the project.

## Which projects can use it

* Java projects (of course, Kotlin and Gradle projects are also acceptable);
* Projects that use relational databases (e.g., MySQL, Oracle, etc.);
* It can be integrated with any framework: Spring Boot, Grails, JFinal, etc.

## When to use it

Every framework has its own usage scenarios. Of course, Bean Searcher is no exception. Its emergence is not to replace traditional ORMs such as MyBatis or Hibernate. Therefore, it is very important to understand which scenarios are suitable for using it.

* **It is recommended** to use it in **non-transactional** and **dynamic** retrieval scenarios. For example:
  In the retrieval scenarios of pages such as [Order Management] and [User Management] in the management background, the retrieval is **non-transactional** and does not insert data into the database. Moreover, the retrieval conditions are **dynamic**. Different user retrieval methods result in different executed SQL statements (e.g., retrieving by `order number` and retrieving by `status` require different SQL statements). In this case, it is recommended to use Bean Searcher for retrieval.
* **It is not recommended** to use it in **transactional** and **static** query scenarios. For example:
  In the user registration interface, where it is necessary to first query whether an account already exists, the interface is **transactional** as it needs to insert data into the database. At this time, the query conditions are **static**. Regardless of which account, the same SQL statement is executed (querying by `account name`). In this case, it is not recommended to use Bean Searcher for the query.

## Which databases are supported

As long as a database supports normal SQL syntax, it is supported. In addition, Bean Searcher has five built-in dialect implementations:

* Databases with the same pagination syntax as MySQL are supported by default.
* For databases with the same pagination syntax as PostgreSQL, select the PostgreSQL dialect.
* For databases with the same pagination syntax as Oracle, select the Oracle dialect.
* For databases with the same pagination syntax as SqlServer (v2012+), select the SqlServer dialect.
* For databases with the same pagination syntax as DaMeng, select the DaMeng dialect (**since v4.6.0**).

If a database has a unique pagination syntax, you only need to customize a dialect by implementing two methods. Refer to the [Advanced > SQL Dialect](/en/guide/advance/dialect) section.

::: tip 🚀 Live Demo
Try declarative search without deployment: [Live Demo](/en/guide/info/demo)
:::
