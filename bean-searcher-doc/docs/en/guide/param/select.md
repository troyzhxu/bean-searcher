# Specify Select Fields

By default, Bean Searcher will query all mapped fields in the entity class. However, you can use the following parameters to specify the fields to be selected:

* `onlySelect` - Specify the fields to be selected.
* `selectExclude` - Specify the fields not to be selected.

## Usage

* Front-end parameter passing form

Refer to the [Getting Started > Usage > Start Searching](/en/guide/start/use.html#_4-Specify-Exclude-Fields-onlyselect-selectexclude) section.

* Parameter builder form

```java
Map<String, Object> params = MapUtils.builder()
        .onlySelect(User::getId, User::getName)     // (1) Only query the id and name fields.
        .onlySelect("id", "name")                   // Equivalent to (1).
        .onlySelect("id,name")                      // Equivalent to (1) (since v3.7.1).
        .selectExclude(User::getAge, User::getDate) // (2) Do not query the age and date fields.
        .selectExclude("age", "date")               // Equivalent to (2).
        .selectExclude("age,date")                  // Equivalent to (2) (since v3.7.1).
        .build();
List<User> users = searcher.searchList(User.class, params);
```

## Configuration Items

In SpringBoot / Grails projects, you can customize the parameter names in the following way:

Configuration Key | Meaning | Optional Values | Default Value
-|-|-|-
`bean-searcher.params.only-select` | Name of the onlySelect parameter | `String` | `onlySelect`
`bean-searcher.params.select-exclude` | Name of the selectExclude parameter | `String` | `selectExclude`
