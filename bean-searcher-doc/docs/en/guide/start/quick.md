---
title: Bean Searcher Quick Start | Get Started in 5 Minutes
description: Write your first search API in 5 minutes. Add dependencies, create an entity class, inject BeanSearcher, and implement complex list retrieval with pagination, filtering, sorting, and statistics — all in one line of code.
head:
  - - meta
    - property: og:title
      content: Bean Searcher Quick Start - 5 Minutes
  - - meta
    - property: og:description
      content: Write your first complex list retrieval API from scratch in 5 minutes.
---
# Quick Start

Get your first search API running in **5 minutes** — no deep reading required.

## 1. Add Dependency

For SpringBoot + Maven, add this to your `pom.xml`:

```xml
<dependency>
    <groupId>cn.zhxu</groupId>
    <artifactId>bean-searcher-boot-starter</artifactId>
    <version>4.8.12</version>
</dependency>
```

::: tip Other build tools
Using Gradle, Solon, or a different JDK version? See [Install](/en/guide/start/install).
:::

## 2. Define a SearchBean

A SearchBean is just a plain Java class that maps to a database table. No annotations needed — the framework auto-detects your fields:

```java
public class User {

    private Long id;
    private String name;
    private int age;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ...
}
```

## 3. Write a Controller

Inject a `MapSearcher` and write a one-liner:

```java
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MapSearcher mapSearcher;

    @GetMapping("/index")
    public SearchResult<Map<String, Object>> index(HttpServletRequest request) {
        // That's it — one line
        return mapSearcher.search(User.class, MapUtils.flat(request.getParameterMap()));
    }
}
```

## 4. Try It Out

Start your app and fire these requests:

```bash
# No parameters (default paging)
GET /user/index

# Search by name
GET /user/index?name=Jack

# Search by age
GET /user/index?age=20

# Age range
GET /user/index?age-op=bt&age=20&age=30

# Pagination + sorting
GET /user/index?page=1&size=10&sort=age&order=desc
```

Response looks like:

```json
{
    "dataList": [
        { "id": 1, "name": "Jack", "age": 25 },
        { "id": 2, "name": "Tom",  "age": 20 }
    ],
    "totalCount": 100
}
```

## Next Steps

You're up and running! Here's where to go next:

- [Start](/en/guide/start/use) — explore the full search API and operators
- [Install](/en/guide/start/install) — other frameworks and JDK versions
- [Why](/en/guide/info/why) — what problems Bean Searcher solves
