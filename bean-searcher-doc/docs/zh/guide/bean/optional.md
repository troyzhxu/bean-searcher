# 可选接口

接口 `BeanAware` 与 `ParamAware` 是 Search Bean 的可选实现接口。实现这个接口的 SearchBean，可以在 afterAssembly 方法里添加 Bean 装配完之后的自定义逻辑。

## BeanAware

实现  `BeanAware` 接口，可在 Bean 装配完之后做一些自定义逻辑逻辑处理：

```java
public class User implements BeanAware {
    // 省略其它代码
    @Override
    public void afterAssembly() {
        // 该方法会在 User 的字段值装配完后自动执行 
        // 代码走到这里说明，说明被 @DbField 注解的字段都已经被赋过值
        System.out.println("id = " + id + ", name = " + name);
    }
}
```

## ParamAware

实现  `ParamAware` 接口，可在 Bean 装配完之后监听到当前的检索参数：

```java
public class User implements ParamAware {
    // 省略其它代码
    @Override
    public void afterAssembly(Map<String, Object> paraMap) {
        // 该方法可接收到当前的检索参数
        System.out.println(paraMap);
    }
}
```
