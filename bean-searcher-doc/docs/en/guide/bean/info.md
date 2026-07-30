# Concept

A retrieval entity class annotated with `@SearchBean` is called a **SearchBean**. In [the previous section](/en/guide/start/use), we demonstrated single-table retrieval. Compared to traditional ORMs, Bean Searcher excels at complex multi-table joins and non-trivial subqueries. Defining a SearchBean for these scenarios is straightforward.

In addition, Bean Searcher also supports [omitting annotations](/en/guide/bean/aignore). An entity class without any annotations can also be automatically mapped to the database.

::: tip Important Note
The entity class (**SearchBean**) mentioned here is a **VO (View Object)** that has a cross-table mapping relationship with the database. It is fundamentally different in concept from the entity class (**Entity**) or domain class (**Domain**) in traditional ORMs.
Refer to the [Introduction > Why Use > Design Concept (Starting Point)](/en/guide/info/why#Design-Concept-Starting-Point) section.
:::
