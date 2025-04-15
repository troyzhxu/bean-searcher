# Optional Interfaces

The interfaces `BeanAware` and `ParamAware` are optional implementation interfaces for Search Beans. SearchBeans that implement these interfaces can add custom logic after the Bean is assembled in the `afterAssembly` method.

## BeanAware

Implementing the `BeanAware` interface allows you to perform some custom logic processing after the Bean is assembled:

```java
public class User implements BeanAware {
    // Omit other code
    @Override
    public void afterAssembly() {
        // This method will be automatically executed after the field values of User are assembled
        // When the code reaches here, it means that the fields annotated with @DbField have all been assigned values
        System.out.println("id = " + id + ", name = " + name);
    }
}
```

## ParamAware

Implementing the `ParamAware` interface allows you to listen to the current retrieval parameters after the Bean is assembled:

```java
public class User implements ParamAware {
    // Omit other code
    @Override
    public void afterAssembly(Map<String, Object> paraMap) {
        // This method can receive the current retrieval parameters
        System.out.println(paraMap);
    }
}
```
