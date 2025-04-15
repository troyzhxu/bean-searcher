# Concepts

Search parameters are important retrieval information in Bean Searcher. Together, they form the value of the second parameter of type `Map<String, Object>` in the retrieval methods of the [`Searcher`](https://gitee.com/troyzhxu/bean-searcher/blob/master/bean-searcher/src/main/java/cn/zhxu/bs/Searcher.java) interface.

::: tip Important Note
The **parameters** in Bean Searcher's retrieval are **decoupled** from the **database table fields**. The **fields** mentioned below all refer to the fields of the **entity class**, that is, the **attributes** of the entity class.
:::

> If you haven't read the [Introduction > Why Use It](/en/guide/info/why) section, it is recommended that you read it first.
