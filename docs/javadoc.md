# Javadoc 常见用法与注意事项

> Java 官方文档注释规范 + JDK / Maven / IDEA 生成方式  
> 建议对照：`DefaultBeanFactory`、`UserService`、`RuntimeBeanReference`

---

## 0. 是什么

**Javadoc** 有两层含义：

1. **写法**：用 `/** ... */` 写在类、接口、方法、字段上的文档注释  
2. **工具**：JDK 自带的 `javadoc` 命令，以及 Maven / IDEA 的文档生成能力

IDE 悬停提示、补全说明，以及生成的 HTML API 文档，都依赖这套注释。

**最小模板**

```java
/**
 * 一句话说明这个类做什么。
 *
 * @since 2026-07-28
 */
public class UserService {
    // ...
}
```

---

## 1. 写法结构

```text
/**
 * 摘要（第一段，悬停时最先看到）
 *
 * <p>可选：更详细的说明段落。</p>
 *
 * @param  name   参数说明
 * @return        返回值说明
 * @throws Xxx    异常说明
 * @see           相关类型/方法
 * @since         引入版本或日期
 * @author        作者
 */
```

要点：

- 必须用 `/**` 开头（两个星号）；`/*` 只是普通块注释，**不会**进 Javadoc  
- 结束用 `*/`（不要写成自定义的奇怪闭合，常规就是 `*/`）  
- 摘要与标签之间空一行，可读性更好  
- HTML 可用，但学习项目里少写即可（常见是 `<p>`、`<pre>`、`<code>`）

---

## 2. 常见标签

| 标签 | 用在 | 说明 |
|------|------|------|
| （无标签首段） | 类/方法/字段 | 摘要，必写最有用的一句 |
| `@param name` | 方法/构造器 | 参数说明；`name` 必须与形参一致 |
| `@return` | 有返回值的方法 | 返回什么；`void` 方法不要写 |
| `@throws` / `@exception` | 方法 | 可能抛出的异常（二者等价，更常用 `@throws`） |
| `@see` | 任意 | 相关链接，如 `@see DefaultBeanFactory#getBean(String)` |
| `@since` | 任意 | 从哪一版/哪天引入 |
| `@author` | 类/接口 | 作者；团队项目可选 |
| `@deprecated` | 任意 | 标记过时，并说明替代方案 |
| `{@link Type}` | 文中 | 可点击链接到类型/成员 |
| `{@link Type#member}` | 文中 | 链接到方法或字段 |
| `{@code expr}` | 文中 | 行内代码样式，如 `{@code null}` |
| `{@literal text}` | 文中 | 按字面输出，不解析 HTML |

### 2.1 类注释示例

```java
/**
 * 简易 Bean 工厂：根据 {@link BeanDefinition} 创建并缓存单例 Bean。
 *
 * @see BeanDefinition
 * @since 2026-07-28
 */
public class DefaultBeanFactory {
}
```

### 2.2 方法注释示例

```java
/**
 * 按名称获取 Bean；若不存在则根据定义创建并缓存。
 *
 * @param beanName Bean 名称，不可为 {@code null}
 * @return 对应的单例实例
 * @throws RuntimeException 反射创建或属性注入失败时
 */
public Object getBean(String beanName) {
    // ...
}
```

### 2.3 字段注释示例（少用即可）

```java
/** 已完成初始化的单例缓存。 */
private final Map<String, Object> singletonBeanMap = new HashMap<>();
```

---

## 3. 本项目建议粒度

| 场景 | 建议 |
|------|------|
| 核心类（如 `DefaultBeanFactory`） | 类级摘要 + 关键 public 方法可写 `@param` / `@return` |
| Demo Bean（如 `UserService`） | 类级一两句即可 |
| getter / setter | **通常不写** Javadoc |
| 测试类 | 可选：说明本 Phase 验证什么 |
| 私有实现细节 | 用 `//` 行注释即可，不必强行 Javadoc |

学习 / mini 项目：**宁可少而准，不要空标签堆砌**。

---

## 4. 如何生成文档

### 4.1 Maven（推荐）

```bash
mvn javadoc:javadoc
```

默认输出：`target/site/apidocs/index.html`，用浏览器打开。

如需在 `pom.xml` 显式声明插件：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.6.3</version>
</plugin>
```

常用目标：

| 目标 | 作用 |
|------|------|
| `javadoc:javadoc` | 生成主代码文档 |
| `javadoc:test-javadoc` | 生成测试代码文档（一般不需要） |
| `javadoc:jar` | 打成 javadoc jar |

### 4.2 IntelliJ IDEA

`Tools` → `Generate JavaDoc...`

- 选择 scope（Module / Package）  
- 指定 Output directory  
- 可勾选包含私有成员等选项  

### 4.3 JDK 命令行

```bash
javadoc -d docs/apidocs -encoding UTF-8 -charset UTF-8 ^
  -sourcepath src/main/java -subpackages com.minispring2
```

（Linux / macOS 把 `^` 换成 `\`。）

---

## 5. 注意事项（易踩坑）

### 5.1 只有标准标签才“合法”

IDEA 的 Javadoc 检查只认规范标签。下面这类**自定义标签会报警**：

```java
/**
 * @description   // 非标准
 * @create ...    // 非标准
 */
```

常见正确替代：

- 描述 → 直接写在注释第一段  
- 创建时间 → `@since 2026-07-28`  

若 IDEA 新建类自动带上这些标签，检查：

`Settings` → `Editor` → `File and Code Templates` → **Includes** → **File Header**

Class 模板里的 `#parse("File Header.java")` 会嵌入该文件头。

### 5.2 `/**` 与 `/*` 不要混用

| 写法 | 是否进 Javadoc / IDE 文档提示 |
|------|-------------------------------|
| `/** ... */` | 是 |
| `/* ... */` | 否（普通注释） |
| `// ...` | 否 |

### 5.3 `@param` 名称必须和形参一致

```java
/** @param beanName 名称 */   // 正确
public Object getBean(String beanName) { }

/** @param name 名称 */       // 错误：形参叫 beanName
public Object getBean(String beanName) { }
```

### 5.4 `{@link}` 写错类型会红线

链接的类要在当前编译路径可解析；必要时写全限定名，或先 `import`。

### 5.5 生成失败常见原因

| 现象 | 可能原因 |
|------|----------|
| 中文乱码 | 加 `-encoding UTF-8 -charset UTF-8` |
| 大量 “warning: no comment” | 缺注释；可用 `-Xdoclint:none` 临时关闭严格检查（不推荐长期依赖） |
| 模块 / JPMS 报错 | Java 9+ 模块路径问题；本项目为普通 classpath，一般无妨 |

Maven 示例（放宽 doclint，仅作权宜）：

```xml
<configuration>
    <doclint>none</doclint>
    <encoding>UTF-8</encoding>
    <charset>UTF-8</charset>
</configuration>
```

### 5.6 注释内容本身的注意点

- 摘要用**第三人称陈述**更常见：“Creates…” / “按名称获取 Bean…”，少用 “我……”  
- 不要复制粘贴无意义的 `@param beanName beanName`  
- `@deprecated` 务必写清**用什么替代**  
- 公开 API 变更时，同步改 Javadoc，避免文档撒谎  

### 5.7 和本仓库的关系

本项目已将 `@description` / `@create` 调整为合法摘要 + `@since`。  
之后若 File Header 未改，新建类仍可能带回非法标签，需改模板才能从根上消除。

---

## 6. 速查清单

写注释前可扫一眼：

- [ ] 使用 `/** ... */`
- [ ] 第一段是有用的摘要
- [ ] 只用标准标签（`@param` / `@return` / `@throws` / `@see` / `@since` / `@author` …）
- [ ] `@param` 名与形参一致
- [ ] `{@link}` / `{@code}` 语法正确
- [ ] 不必给每个 getter/setter 写文档
- [ ] 生成文档时注意 UTF-8 编码

---

## 7. 延伸阅读

- Oracle：[How to Write Doc Comments for the Javadoc Tool](https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html)  
- JDK 工具：`javadoc --help`  
- 本仓库相关：`docs/junit-jupiter-api.md`（测试侧）、`docs/java-reflection-api.md`（反射侧）
