# JUnit Jupiter（org.junit.jupiter.api）接口使用文档

> 版本对照：本项目 `junit.version = 5.10.2`（`pom.xml`）  
> 依赖坐标：`org.junit.jupiter:junit-jupiter`  
> 建议对照：`Phase1Test` / `Phase2Test`

---

## 0. 总览

```
org.junit.jupiter.api
    ├── 注解   @Test / @BeforeEach / @DisplayName / ...
    ├── 断言   Assertions.assertXxx(...)
    ├── 假设   Assumptions.assumeXxx(...)   // 条件不满足则跳过
    ├── 嵌套   @Nested
    ├── 禁用   @Disabled
    └── 超时   @Timeout / assertTimeout
```

**最小模板（你现在的写法）**

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class Phase1Test {

    @Test
    void getBeanTest() {
        // arrange → act → assert
        assertSame(userService, userService1);
    }
}
```

**注意事项**

- JUnit 5 测试类、测试方法**不必** `public`（Jupiter 默认即可访问）。
- 入口包是 `org.junit.jupiter.api`，不是旧版 `org.junit`（JUnit 4）。
- Maven 需 Surefire **2.22+**（本项目 3.2.5），否则可能扫不到 Jupiter 测试。

---

## 1. 测试发现与命名

### 1.1 `@Test`

```java
/**
 * 标记一个测试方法。由 Jupiter 引擎发现并执行。
 * 方法：无参；不要返回值（返回值会被忽略）。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test { }
```

```java
@Test
void shouldReturnSameSingleton() { ... }
```

**注意事项**

- 方法名建议见名知意；失败时报告里会显示方法名。
- 不要在 `@Test` 方法上再加业务参数（除非配合 `@ParameterizedTest`）。
- 一个方法只验证一件事，断言失败会立刻结束该方法（默认）。

---

### 1.2 `@DisplayName` / `@DisplayNameGeneration`

```java
/**
 * 给人看的测试名称，IDE / 报告里替代方法名。
 */
@DisplayName("Phase1: 单例 map 存取应返回同一实例")
@Test
void getBeanTest() { ... }

/**
 * 按规则自动生成显示名（如替换下划线为空格）。
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class some_phase1_tests { }
```

**注意事项**

- `@DisplayName` 支持中文、空格、符号，适合 Phase 学习报告。
- 类与方法上都可以加。

---

### 1.3 `@Tag`

```java
/**
 * 给测试打标签，便于过滤执行（CI 只跑 slow / phase1 等）。
 */
@Tag("phase1")
@Test
void getBeanTest() { ... }
```

Maven 过滤示例：

```xml
<plugin>
  <artifactId>maven-surefire-plugin</artifactId>
  <configuration>
    <groups>phase1</groups>           <!-- 只跑带 @Tag("phase1") -->
    <!-- <excludedGroups>slow</excludedGroups> -->
  </configuration>
</plugin>
```

---

## 2. 生命周期回调

执行顺序（每个测试方法）：

```
@BeforeAll（静态，类级一次）
  → @BeforeEach
    → @Test
  → @AfterEach
@AfterAll（静态，类级一次）
```

```java
/**
 * 所有测试前执行一次。默认必须是 static（除非测试类用 @TestInstance(Lifecycle.PER_CLASS)）。
 */
@BeforeAll
static void initAll() { ... }

/**
 * 每个 @Test 之前执行。适合 new Factory、准备 BeanDefinition。
 */
@BeforeEach
void setUp() {
    factory = new DefaultBeanFactory();
}

/**
 * 每个 @Test 之后执行。适合清理、重置静态状态。
 */
@AfterEach
void tearDown() { ... }

/**
 * 所有测试后执行一次。同样默认 static。
 */
@AfterAll
static void tearDownAll() { ... }
```

**`@TestInstance`**

```java
/**
 * PER_METHOD（默认）：每个测试新建测试类实例。
 * PER_CLASS：整个类共用一个实例，@BeforeAll 可以是实例方法。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Phase2Test { ... }
```

**注意事项**

- 默认每个 `@Test` 新实例 → 实例字段互不影响；不要依赖「上一个测试留下的字段」。
- `@BeforeAll` / `@AfterAll` 忘记 `static` 会启动失败（除非 `PER_CLASS`）。
- 生命周期方法里抛出的异常会导致测试失败/中止。

---

## 3. Assertions — 断言 API

包：`org.junit.jupiter.api.Assertions`  
推荐：`import static org.junit.jupiter.api.Assertions.*;`

### 3.1 相等 / 同一 / 空

```java
/** 值相等（走 equals）；浮点请用带 delta 的重载 */
assertEquals(expected, actual);
assertEquals(expected, actual, "失败时的说明");
assertEquals(1.0, actual, 0.001);   // double delta

/** 同一引用（==）。单例测试首选 */
assertSame(userService, fromFactory);
assertNotSame(a, b);

/** null */
assertNull(obj);
assertNotNull(obj);
assertNotNull(obj, "getBean 不应返回 null");
```

**与本项目对照**

| 场景 | 推荐断言 |
|------|----------|
| Phase1 单例同一实例 | `assertSame` |
| getBean 非空 | `assertNotNull` |
| 依赖注入后字段值 | `assertSame(dao, service.getUserDao())` |
| 字符串/数字结果 | `assertEquals` |

**注意事项**

- `assertEquals` 看 **equals**；要证明单例用 **`assertSame`**，不要混用。
- 失败消息放在**最后一个**参数（String 或 `Supplier<String>`），避免每次都拼接昂贵字符串时用 Supplier。

---

### 3.2 真假 / 数组 / 集合相关

```java
assertTrue(condition);
assertFalse(condition);

assertArrayEquals(expectedArr, actualArr);
assertIterableEquals(expectedList, actualList);
assertLinesMatch(expectedLines, actualLines);  // 支持正则行
```

---

### 3.3 异常断言（强烈推荐）

```java
/**
 * 执行可执行块，断言抛出指定类型异常，并返回异常实例便于再断言。
 */
Exception ex = assertThrows(IllegalArgumentException.class, () -> {
    factory.getBean("missing");
});
assertEquals("No bean named missing", ex.getMessage());

/** 断言不抛异常 */
assertDoesNotThrow(() -> factory.getBean("userService"));
```

**注意事项**

- ❌ 不要再用 try/catch + `fail()` 的老写法。
- 只断言「抛了异常」不够时，继续对返回的异常查 message / cause。

---

### 3.4 超时

```java
/** 断言在时限内完成，超时则失败 */
assertTimeout(Duration.ofMillis(100), () -> {
    factory.getBean("userService");
});

/**
 * 超时后抢占中断（另起线程执行）。
 * 注意：被测代码若不响应中断，仍可能拖住。
 */
assertTimeoutPreemptively(Duration.ofSeconds(1), () -> { ... });
```

也可用注解：

```java
@Test
@Timeout(value = 2, unit = TimeUnit.SECONDS)
void createBeanShouldBeFast() { ... }
```

---

### 3.5 组合断言

```java
/**
 * 多个断言都会执行，最后汇总失败信息（不会在第一个失败处停下）。
 */
assertAll("UserService 注入校验",
    () -> assertNotNull(service),
    () -> assertNotNull(service.getUserDao()),
    () -> assertSame(dao, service.getUserDao())
);
```

**注意事项**

- 想「看全部分失败点」用 `assertAll`；想「快速失败」用普通顺序断言。

---

### 3.6 直接失败

```java
fail("还不该走到这里");
fail(() -> "懒加载消息");
```

---

## 4. Assumptions — 条件跳过（不是失败）

```java
import static org.junit.jupiter.api.Assumptions.*;

/**
 * 条件为 false → 测试标记为 aborted（跳过），不算失败。
 * 适合：仅 Linux 跑、仅有某环境变量时跑。
 */
assumeTrue(System.getenv("CI") != null);
assumingThat("dev".equals(profile), () -> {
    // 仅当条件满足才执行这段
});
```

**注意事项**

- Assumption 失败 = **跳过**；Assertion 失败 = **失败**。CI 统计含义不同。
- 业务正确性验证请用 Assertion，不要用 Assumption。

---

## 5. 禁用与条件执行

```java
/** 整个类或方法跳过，报告中为 disabled */
@Disabled("Phase3 循环依赖尚未实现")
class Phase3Test { }

@Disabled
@Test
void wip() { }

/** 按系统属性 / 环境 / OS / JRE 条件启用 */
@EnabledOnOs(OS.WINDOWS)
@DisabledOnOs(OS.LINUX)
@EnabledOnJre(JRE.JAVA_17)
@EnabledIfSystemProperty(named = "env", matches = "test")
@EnabledIfEnvironmentVariable(named = "RUN_SLOW", matches = "true")
```

包名：`org.junit.jupiter.api.condition.*`

**注意事项**

- 提交前临时注释测试不如加 `@Disabled("原因")`，原因会进报告。
- 条件注解可叠在类或方法上。

---

## 6. 嵌套测试 `@Nested`

```java
/**
 * 组织同一类下的场景分组；内部类需非 static。
 */
class DefaultBeanFactoryTest {

    DefaultBeanFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultBeanFactory();
    }

    @Nested
    @DisplayName("Phase1 单例")
    class Singleton {
        @Test
        void sameInstance() { ... }
    }

    @Nested
    @DisplayName("Phase2 依赖注入")
    class Di {
        @Test
        void injectUserDao() { ... }
    }
}
```

**注意事项**

- `@Nested` 内部类不能是 `static`。
- 外层 `@BeforeEach` 会在内层测试前执行（按层级链）。

---

## 7. 重复与参数化（常用扩展）

> 参数化在 `org.junit.jupiter.params`（已含于 `junit-jupiter` 聚合包）。

```java
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

@ParameterizedTest
@ValueSource(strings = { "userService", "userDao" })
void beanNameShouldNotBeBlank(String name) {
    assertFalse(name.isBlank());
}

@ParameterizedTest
@CsvSource({
    "userService, com.minispring2.demo.model.UserService",
    "userDao,     com.minispring2.demo.dao.UserDao"
})
void registerByName(String beanName, String className) { ... }

@RepeatedTest(3)
@DisplayName("创建 Bean 稳定可重复")
void createBeanStable() { ... }
```

**注意事项**

- 参数化方法用 `@ParameterizedTest`，**不要**再叠一个普通 `@Test`（会变成两次发现/异常配置）。
- `@MethodSource` 的工厂方法需 `static`，返回 `Stream` / `Iterable` 等。

---

## 8. 测试接口与默认方法（可选）

```java
/**
 * 可在接口上定义 @Test / @BeforeEach，实现类自动继承测试。
 */
interface FactorySmokeTests {
    DefaultBeanFactory factory();

    @Test
    default void factoryNotNull() {
        assertNotNull(factory());
    }
}
```

---

## 9. 常用导入速查

```java
// 注解
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.TestInstance;

// 断言 / 假设
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

// 条件
import org.junit.jupiter.api.condition.*;

// 参数化（扩展包）
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
```

---

## 10. 与 JUnit 4 对照（防混用）

| JUnit 4 | JUnit Jupiter |
|---------|----------------|
| `org.junit.Test` | `org.junit.jupiter.api.Test` |
| `@Before` / `@After` | `@BeforeEach` / `@AfterEach` |
| `@BeforeClass` / `@AfterClass` | `@BeforeAll` / `@AfterAll` |
| `@Ignore` | `@Disabled` |
| `Assert.assertEquals` | `Assertions.assertEquals` |
| `@RunWith` | 扩展模型 `@ExtendWith`（如 MockitoExtension） |
| `@Test(expected=...)` | `assertThrows` |

**注意事项**

- 同一测试类不要混用 JUnit 4 / 5 注解，Surefire 可能只跑其中一套。
- 本项目只引入了 Jupiter，请统一用 5.x API。

---

## 11. 针对 mini-Spring Phase 的推荐写法

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Phase2: RuntimeBeanReference 依赖注入")
class Phase2Test {

    DefaultBeanFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultBeanFactory();
    }

    @Test
    @DisplayName("getBean(userService) 应注入同一 userDao 单例")
    void shouldInjectUserDao() {
        // ... 注册 BeanDefinition ...

        UserService service = (UserService) factory.getBean("userService");
        UserDao dao = (UserDao) factory.getBean("userDao");

        assertAll(
            () -> assertNotNull(service),
            () -> assertNotNull(dao),
            () -> assertSame(dao, service.getUserDao())
        );
    }

    @Test
    @DisplayName("缺少依赖定义时应抛出明确异常")
    void missingDependency() {
        // ...
        assertThrows(RuntimeException.class,
            () -> factory.getBean("userService"));
    }
}
```

---

## 12. 运行方式

```bash
# 跑全部测试
mvn test

# 只跑某个类
mvn test -Dtest=Phase1Test

# 只跑某个方法
mvn test -Dtest=Phase2Test#shouldInjectUserDao
```

IDEA：方法旁绿灯运行；失败时看断言期望值 / 实际值对比。

---

## 13. Checklist

- [ ] 使用 `org.junit.jupiter.api.Test`，不是 `org.junit.Test`
- [ ] 静态导入 `Assertions.*`，断言消息放最后一参
- [ ] 单例用 `assertSame`，非空用 `assertNotNull`，异常用 `assertThrows`
- [ ] 共享初始化放 `@BeforeEach`，不要复制粘贴到每个测试
- [ ] 未完成用例用 `@Disabled("原因")`，不要长期注释掉
- [ ] 参数化用 `@ParameterizedTest`，不要与 `@Test` 叠用
- [ ] `@BeforeAll` 默认需要 `static`

---

## 14. 异常 / 现象速查

| 现象 | 原因 | 处理 |
|------|------|------|
| 测试不被执行 | 用了 JUnit4 的 `@Test` 或 Surefire 过旧 | 换 Jupiter 注解；检查 surefire 版本 |
| `@BeforeAll` 报错 | 非 static 且默认 PER_METHOD | 加 `static` 或 `@TestInstance(PER_CLASS)` |
| `assertEquals` 单例「通过」但实际是两实例 | 用错了 equals 断言 | 改用 `assertSame` |
| 参数化测试不跑 / 配置错误 | 同时标了 `@Test` + `@ParameterizedTest` | 只保留 `@ParameterizedTest` |
| 中文 `@DisplayName` 乱码 | 控制台编码 | IDE 用 UTF-8；Maven 已设 `project.build.sourceEncoding` |
