# JDK 动态代理与 CGLIB 使用文档

> 面向 Spring AOP / mini-Spring 代理场景的速查文档。  
> JDK：`java.lang.reflect.Proxy`、`InvocationHandler`（JDK 自带）  
> CGLIB：`net.sf.cglib.proxy.Enhancer`、`MethodInterceptor`（第三方，本仓库 `cglib.version = 3.3.0`）  
> 建议对照：`com.minispring2.aop.JdkDynamicProxy`、后续 CGLIB 封装类

---

## 0. 总览

```text
调用方 → 代理对象
              ├── JDK Proxy：实现「目标类的接口」，InvocationHandler 拦截
              └── CGLIB：   继承「目标类」，MethodInterceptor 拦截
                    ↓
              可选：再调用真实目标对象（target）
```

| | JDK 动态代理 | CGLIB |
|--|-------------|--------|
| 依赖 | JDK 自带 | 需引入 `cglib`（或 Spring 自带的包装） |
| 原理 | 实现接口 | 继承类（子类） |
| 目标限制 | **必须有接口** | 类不能是 `final`，方法最好非 `final` |
| 典型包 | `java.lang.reflect.Proxy` | `net.sf.cglib.proxy.Enhancer` |
| Spring 默认偏好 | 有接口时常用 JDK（经典） | 无接口或 `proxyTargetClass=true` 时用 CGLIB |

Spring 里代理多在 `BeanPostProcessor#postProcessAfterInitialization` 中套上；业务侧常见于 `@Transactional`、`@Cacheable`、自定义 `@Aspect`。

---

## 1. 何时用代理（业务视角）

不改业务类源码，却要在方法调用前后加统一逻辑时用代理，例如：

- 开/关事务、回滚  
- 读缓存 / 写缓存（可能直接返回缓存，不进原方法）  
- 权限校验、日志、耗时统计、重试  

调用方拿到的往往是**代理**，不是原始 Bean。

---

## 2. JDK 动态代理

### 2.1 核心 API

```java
static Object Proxy.newProxyInstance(
        ClassLoader loader,      // 一般用目标类的 ClassLoader
        Class<?>[] interfaces,   // 要代理的接口（可多个）
        InvocationHandler h);    // 拦截逻辑

// 回调
Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
```

| 参数 | 含义 |
|------|------|
| `proxy` | 代理对象自身（几乎不要对它再 `method.invoke`） |
| `method` | 被调用的接口方法 |
| `args` | 实参；无参时可能为 `null`，注意 NPE |

### 2.2 最小可用示例

```java
public interface OrderService {
    Order getOrder(Long id);
}

public class OrderServiceImpl implements OrderService {
    @Override
    public Order getOrder(Long id) {
        return orderDao.findById(id);
    }
}

OrderService target = new OrderServiceImpl();

OrderService proxy = (OrderService) Proxy.newProxyInstance(
        target.getClass().getClassLoader(),
        target.getClass().getInterfaces(),  // 或 new Class[]{ OrderService.class }
        (p, method, args) -> {
            System.out.println("before " + method.getName());
            Object result = method.invoke(target, args); // 调目标，不是 proxy
            System.out.println("after " + method.getName());
            return result;
        }
);

Order order = proxy.getOrder(1L); // 走 InvocationHandler
```

### 2.3 使用说明

1. 目标类必须实现至少一个接口；代理类型是「接口的实现类」，**不是** `OrderServiceImpl` 的子类。  
2. 强制转换请转到**接口**：`(OrderService) proxy`，不要转成实现类。  
3. `method.invoke` 的第一个参数必须是 **target**，不能是 `proxy`（否则死循环）。  
4. 只拦截**接口上声明**的方法；实现类自己多出来的 public 方法，经接口引用调用不到，也就拦不到。  
5. `hashCode` / `equals` / `toString` 也会进 `invoke`，写通用拦截时注意别误伤（可按 `method.getDeclaringClass() == Object.class` 过滤）。

### 2.4 JDK 注意事项

| 注意点 | 说明 |
|--------|------|
| 只能代理接口 | 没有接口 → 改用 CGLIB，或先抽接口 |
| 返回值 | 必须与方法签名兼容；基本类型不能返回 `null` |
| 受检异常 | `invoke` 若抛出方法签名未声明的受检异常，会被包成 `UndeclaredThrowableException` |
| 自调用 | 目标类内部 `this.xxx()` **不会**走代理（和 Spring AOP 一样） |
| `args` 可能为 null | 无参方法建议 `args == null ? new Object[0] : args` |
| 性能 | 现代 JDK 下通常足够；热点路径再考虑对比 CGLIB |

---

## 3. CGLIB

### 3.1 依赖（Maven）

本仓库版本属性：`cglib.version = 3.3.0`。模块中实际使用时需加入依赖（勿只写在 `dependencyManagement` 里）：

```xml
<dependency>
    <groupId>cglib</groupId>
    <artifactId>cglib</artifactId>
    <version>${cglib.version}</version>
</dependency>
```

> Spring Framework 自带了对 CGLIB 的重打包（`spring-core` 等），正式 Spring 项目往往不必单独引 `cglib`。本 mini 项目若手写 Enhancer，需要显式依赖。

### 3.2 核心 API

```java
Enhancer enhancer = new Enhancer();
enhancer.setSuperclass(OrderServiceImpl.class);  // 被代理的类（父类）
enhancer.setCallback(new MethodInterceptor() {
    @Override
    public Object intercept(Object obj, Method method, Object[] args,
                            MethodProxy proxy) throws Throwable {
        System.out.println("before " + method.getName());
        // 方式 A：反射调父类逻辑（需有 target 时也可用 method.invoke(target, args)）
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("after " + method.getName());
        return result;
    }
});
OrderServiceImpl proxy = (OrderServiceImpl) enhancer.create();
```

| 回调成员 | 含义 |
|----------|------|
| `obj` | CGLIB 生成的子类实例（代理本身） |
| `method` | 被拦截的方法 |
| `args` | 实参 |
| `MethodProxy` | CGLIB 提供的快速调用；`invokeSuper(obj, args)` 调父类方法实现 |

### 3.3 使用说明

1. 生成的是目标类的**子类**，可强制转换为具体类：`(OrderServiceImpl) proxy`。  
2. 无接口的类也能代理（Spring 里 `proxy-target-class=true` 常见）。  
3. 优先用 `MethodProxy.invokeSuper` 调「父类方法体」；若手里有独立 target 实例，也可用 `method.invoke(target, args)`，但不要对代理再套一层乱调。  
4. `enhancer.create()` 会调用子类构造器；目标类需要**可访问的无参构造**（或配合 `create(Class[], Object[])` 传参）。  
5. 默认会拦截各类方法；可用 `CallbackFilter` 指定哪些方法走哪个 Callback（进阶）。

### 3.4 CGLIB 注意事项

| 注意点 | 说明 |
|--------|------|
| `final` 类 | **无法**继承 → 不能代理 |
| `final` 方法 | 子类不能覆盖 → **不会被拦截** |
| `private` 方法 | 不能被覆盖 → 不会被拦截 |
| 静态方法 | 不走实例多态 → 不会被拦截 |
| 构造器 | `create()` 会跑构造逻辑；构造器里调的实例方法，行为要小心（对象未完全就绪） |
| Java 模块 | JDK 17+ 深反射/生成类可能需 `--add-opens`（本项目 Surefire 已有示例） |
| 与 JDK Proxy 并存 | 同一体系里选一种策略并文档化，避免有的 Bean 是接口代理、有的是类代理导致强转失败 |

---

## 4. 对照与选型

```text
目标实现了接口？
    ├── 是 → JDK Proxy（简单、无额外依赖）或 CGLIB（强制类代理时）
    └── 否 → CGLIB（或改为面向接口再 JDK）

类或关键方法是 final？
    └── 是 → CGLIB 拦不住 / 类直接失败 → 去掉 final 或换设计
```

| 需求 | 更合适 |
|------|--------|
| 面向接口编程、依赖仅 JDK | JDK Proxy |
| 具体类、无接口、要强转实现类 | CGLIB |
| Spring `@Transactional` 打在实现类且无接口 | CGLIB（或抽接口） |
| 希望拦截具体类上的方法名 | CGLIB（仍受 final/private 限制） |

---

## 5. 和 Spring / mini-Spring 的关系

### 5.1 在容器中的位置

```text
instantiate → populate → initialize
  → BeanPostProcessor.postProcessAfterInitialization
       └── 若需要增强：返回 JDK/CGLIB 代理
  → 单例池放入代理
```

之后 `getBean` / 依赖注入拿到的常常是代理。循环依赖时，三级缓存的 early 引用也要和「是否提前暴露代理」策略一致（后续 AOP Phase 再深入）。

### 5.2 「代理改变返回结果」业务例（缓存）

```java
@Cacheable("orders")
public Order getOrder(Long id) { return dao.find(id); }
```

代理逻辑伪码：

```text
if (cache hit) return cached;   // 原方法不执行，返回值来源变了
else { Order o = target.getOrder(id); cache.put(...); return o; }
```

### 5.3 自调用陷阱（两种代理都中招）

```java
public void a() { this.b(); }   // this 是目标对象，不是代理

@Transactional
public void b() { ... }
```

外部调 `a()` 若未再进代理，`b()` 的事务可能不生效。解法：注入自身代理、`AopContext.currentProxy()`、或拆到另一个 Bean。

---

## 6. 常见报错

| 现象 | 可能原因 | 处理 |
|------|----------|------|
| `ClassCastException: $Proxy cannot be cast to XxxImpl` | JDK 代理转成了实现类 | 依赖接口类型，或改用 CGLIB |
| CGLIB：`Cannot subclass final class` | 目标类 `final` | 去 `final` 或抽接口用 JDK |
| 增强不生效 | `final`/`private` 方法、或 `this` 自调用 | 见上文限制与自调用 |
| `UndeclaredThrowableException` | JDK handler 抛了未声明受检异常 | 在 handler 里转成运行时异常，或对齐方法 throws |
| 死循环 / StackOverflow | `method.invoke(proxy, ...)` | 改为 invoke **target** 或 `invokeSuper` |
| JDK 17 模块错误 | 生成/访问受限 | `--add-opens` 等（参考本项目 Surefire `argLine`） |

---

## 7. 速查清单

**JDK Proxy**

- [ ] 目标有接口；按接口接收代理  
- [ ] `invoke` 里调用 `target`，不是 `proxy`  
- [ ] 处理好 `Object` 方法与返回值类型  

**CGLIB**

- [ ] 依赖已引入（或走 Spring 自带）  
- [ ] 类非 `final`，关键方法尽量非 `final`  
- [ ] 有合适构造器；拦截里用 `invokeSuper` 或明确的 target  

**通用**

- [ ] 清楚「容器里是代理」；避免错误强转  
- [ ] 警惕自调用导致切面失效  
- [ ] 与 IoC 生命周期（AfterInitialization）对齐理解  

---

## 8. 延伸阅读

- JDK：`java.lang.reflect.Proxy`、`InvocationHandler`  
- CGLIB：`net.sf.cglib.proxy.Enhancer`、`MethodInterceptor`、`MethodProxy`  
- 本仓库：`docs/java-reflection-api.md`（§6 有 JDK Proxy 摘要）、`docs/javadoc.md`  
- Spring：`ProxyFactory` / `AopProxy`（JDK / CGLIB 封装入口，读源码时可从这里跟）
