---
title: Why Bean Searcher? | Bean Searcher Documentation
description: "Discover how Bean Searcher solves the pain points of traditional Java list retrieval: eliminates boilerplate code, boosts productivity, and includes built-in security controls."
head:
  - - meta
    - property: og:title
      content: Why Choose Bean Searcher?
  - - meta
    - property: og:description
      content: Eliminate boilerplate code — complex list retrieval with one line of code.
---
# Why Use It

![需求图](/requirement.png)

The product manager has drawn a diagram for you and attached some requirements:

* Display search results in pages.
* Sort by any field.
* Count certain field values according to search criteria.

At this time, how should the backend api be written? Would it take more than 100 lines of code if you use MyBatis or Hibernate? However, with Bean Searcher, you can achieve the above requirements with just **one line of code**!

> **Bean Searcher is to list retrieval what GraphQL is to API queries.** The difference: GraphQL requires a dedicated protocol and schema; Bean Searcher works on standard HTTP parameters — no protocol changes, no middleware layer, just add one dependency.

## From VO to SearchBean (The Birth of Declarative Search)

Any system has list retrieval requirements (order management, user management, etc.), and the data on a list page often spans multiple database tables. For example, in an order management page: the order number comes from the orders table, the user name comes from the users table — backend **domain classes** (entities mapped to a single table) simply don't match what the user sees on the page.

To bridge this gap, **VO (View Object)** was born. VO isn't tied to database tables — it faces the page directly. Sounds elegant, but the cost is: you need an extra translation layer that converts domain query results into VO. The more complex the filtering criteria, the more bloated the translation logic becomes.

Bean Searcher's answer: **why can't a VO directly map to the database?** If VO is already built for the page, why not let it "declare" which tables it relates to and which filter conditions it accepts?

This upgraded VO is called a **SearchBean** — it faces the page AND directly maps to multiple database tables, unifying "declaration" and "retrieval":

> **SearchBean = VO (page-facing) + cross-table mapping (database-facing)**

This brings a fundamental shift: every field in a SearchBean is not only data the page displays, but also a condition the frontend can search by. **The SearchBean IS the interface's declaration** — whatever parameters the frontend sends, the framework generates the corresponding SQL. No conversion code needed.

Therefore, one SearchBean per retrieval interface:
* Order list → one SearchBean
* User list → one SearchBean
* Data export → reuse the same SearchBean (same retrieval logic)

## Why Such a Big Efficiency Gain

Compare the traditional approach with declarative search in terms of code:

**Traditional**: Write SQL / QueryWrapper → query domain class → convert to VO → pile on if-else for every filter condition. Every time a requirement or VO changes, you touch 3-4 layers of code.

**Declarative Search**: Define SearchBean (one-step VO) → inject BeanSearcher → one `search()` call. Frontend adds filters, changes sort order, adjusts page size — all via parameters, **zero backend changes**. You only touch the SearchBean when you need new searchable fields.

Because the SearchBean declares the search boundary and the `search()` method drives the query — there's no conversion code to insert between "declaration" and "execution". The returned SearchBean IS the data the page needs, ready for the frontend.

That's the fundamental reason **one line of code can handle complex list retrieval**.

## Does the Front-end Need to Pass More Parameters?

Many people new to Bean Searcher **mistakenly assume** that using it will put pressure on the front end and **require extra parameters that were never needed before**.

Actually, it's not the case. The **number of parameters** that the front end needs to pass is only related to the **complexity of the product requirements** and has nothing to do with the **backend framework** used.

You might wonder: I've seen many articles about Bean Searcher that mention parameters like **xxx-op** and **xxx-ic**. There are no such parameters in our system. Do we need to pass them after using Bean Searcher?

Note that the content in those articles is about **advanced queries**. The product requires the front end to be able to control whether a certain field is searched by fuzzy matching or exact matching, as shown in the following figure:

![](/requirement_1.png)

But what if the front end doesn't need this? For example, for the `username` field, the front end only needs a fuzzy query and doesn't need to ignore case. Do we still need to pass the `username-op` and `username-ic` parameters in the backend?

**Of course not**. You only need to pass the `username` parameter. So how does the backend express the **fuzzy query** condition? It's very simple. Just add an annotation to the `username` attribute in the SearchBean:

```java
@DbField(onlyOn = Contain.class)
private String username;
```

You can refer to the [Advanced > Constraints and Risk Control](/en/guide/advance/safe) section.
