# Concept

The retrieval entity class, which is the class annotated with `@SearchBean`, is also known as **SearchBean**. In [the previous section](/en/guide/start/use), we experienced the single-table retrieval function of Bean Searcher. However, compared with traditional ORMs, it is actually better at handling complex joint-table retrievals and some strange subqueries. At this time, the definition of SearchBean is also very easy.

In addition, Bean Searcher also supports [omitting annotations](/en/guide/bean/aignore). An entity class without any annotations can also be automatically mapped to the database.

::: tip Important Note
The entity class (**SearchBean**) mentioned here is a **VO (View Object)** that has a cross-table mapping relationship with the database. It is fundamentally different in concept from the entity class (**Entity**) or domain class (**Domain**) in traditional ORMs.
Refer to the [Introduction > Why Use > Design Concept (Starting Point)](/en/guide/info/why#Design-Concept-Starting-Point) section.
:::
