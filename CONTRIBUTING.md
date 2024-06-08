## 贡献流程

* Fork 本仓库
* 切换到目标分支（一般是 dev 分支，如果是修复特定版本，则切到特定版本的分支）
* 提交功能代码 与 编写单元测试
* 新建 Pull Request

## 单元测试规范

* 测试类名必须以 `XxxTestCase` 方式命名，其中 `Xxx` 要体现被测试主体或功能，例如；`DefaultDbMappingTestCase`、`OrderByTestCase` 等。
* 测试方法必须以 `test_` 开头，被 `public` 修饰，`void` 返回，并使用 `@Test` 注解
* 测试结果必须以 `org.junit.jupiter.api.Assertions` 的相关 API 进行验证

## 编码规范

* 请参考源代码
