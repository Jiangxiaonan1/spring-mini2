# Java 常用反射接口文档

> 面向 IoC / Bean 工厂场景的速查文档。  
> JDK 包：`java.lang.Class`、`java.lang.reflect.*`  
> 建议对照：`DefaultBeanFactory#instantiate`

---

## 0. 总览

```
Class / Class.forName
    ├── Constructor  → newInstance(...)     // 实例化
    ├── Method       → invoke(obj, args)    // 调方法 / setter
    ├── Field        → get / set            // 读写字段
    └── Annotation   → getAnnotation(...)   // 读注解元数据
```

**通用约定**

| 前缀 | 含义 |
|------|------|
| `getXxx` | 仅 **public**，可查到父类 |
| `getDeclaredXxx` | 本类声明的全部（含 private），**不含父类** |

---

## 1. Class — 类型入口

### 1.1 获取 Class 对象

```java
/**
 * 按全限定名加载类（会触发类初始化，除非用重载控制）。
 * @param className 如 "com.demo.User"
 * @throws ClassNotFoundException 类路径找不到
 */
Class<?> Class.forName(String className);

/**
 * 从已有实例反推运行时类型（可能是子类）。
 */
Class<?> obj.getClass();

/**
 * 编译期已知类型的字面量写法，最安全、最快。
 */
Class<User> clazz = User.class;
```

**注意事项**

- `Class.forName` 会执行静态块；仅要 `Class` 对象时可用 `ClassLoader.loadClass` 避免初始化。
- 基本类型与包装类型是不同 `Class`：`int.class` ≠ `Integer.class`。
- 数组类型写法：`String[].class`、`int[].class`。

---

### 1.2 类型判断与继承

```java
/** 是否接口 / 注解 / 枚举 / 数组 / 基本类型 */
boolean isInterface();
boolean isAnnotation();
boolean isEnum();
boolean isArray();
boolean isPrimitive();

/** 父类；接口返回 null；Object 返回 null */
Class<?> getSuperclass();

/** 直接实现的接口 */
Class<?>[] getInterfaces();

/**
 * 运行时类型检查：obj 能否赋给本 Class 表示的类型。
 * 等价于 instanceof，但右侧是动态 Class。
 */
boolean isInstance(Object obj);

/**
 * 类型兼容：other 能否转换到本类型（含自动拆装箱等规则）。
 */
boolean isAssignableFrom(Class<?> other);
```

**注意事项**

- `isAssignableFrom`：参数是「候选子类型」。例：`List.class.isAssignableFrom(ArrayList.class)` → true。
- 泛型擦除后反射拿不到 `List<String>` 的 `String`（需 `ParameterizedType`，进阶场景再用）。

---

## 2. Constructor — 构造器 / 实例化

```java
/**
 * 获取 public 构造器；参数类型必须精确匹配。
 * @param parameterTypes 构造参数的 Class 列表；无参则省略
 * @throws NoSuchMethodException 找不到匹配签名
 */
Constructor<T> getConstructor(Class<?>... parameterTypes);

/**
 * 获取本类声明的构造器（含 private / protected）。
 * IoC 无参实例化推荐用这个。
 */
Constructor<T> getDeclaredConstructor(Class<?>... parameterTypes);

/** 列出全部 public / 本类全部构造器 */
Constructor<?>[] getConstructors();
Constructor<?>[] getDeclaredConstructors();

/**
 * 调用构造器创建实例。
 * @param initargs 实参，个数与类型需匹配
 * @throws InstantiationException 抽象类、接口无法实例化
 * @throws IllegalAccessException 可见性不足且未 setAccessible
 * @throws InvocationTargetException 构造器内部抛出的异常被包装
 * @throws IllegalArgumentException 参数个数/类型不匹配
 */
T newInstance(Object... initargs);

/**
 * 关闭访问检查，允许调用 private 构造器。
 * Java 9+ 模块系统下仍可能被拒绝（InaccessibleObjectException）。
 */
void setAccessible(boolean flag);
```

**推荐写法（替代已废弃的 Class.newInstance）**

```java
Object bean = clazz.getDeclaredConstructor().newInstance();
```

**注意事项**

- ❌ 不要用已废弃的 `Class.newInstance()`。
- 无 public 无参构造 → `getConstructor()` 失败；改用 `getDeclaredConstructor()` + 必要时 `setAccessible(true)`。
- `InvocationTargetException.getCause()` 才是业务里真正的异常。
- 内部类非 static 的构造器隐藏了外部类实例参数，反射时容易踩坑。

---

## 3. Method — 方法调用

```java
/**
 * 公共方法（含父类）。找不到则 NoSuchMethodException。
 * @param name 方法名，如 "setUserDao"
 * @param parameterTypes 形参类型，必须精确匹配
 */
Method getMethod(String name, Class<?>... parameterTypes);

/**
 * 本类声明的方法（含 private），不含父类方法。
 * 属性注入找 setter 时常用。
 */
Method getDeclaredMethod(String name, Class<?>... parameterTypes);

Method[] getMethods();
Method[] getDeclaredMethods();

/**
 * 执行方法。
 * @param obj 实例；静态方法传 null
 * @param args 实参
 * @return 返回值；返回类型为 void 时为 null
 */
Object invoke(Object obj, Object... args);

void setAccessible(boolean flag);

/** 方法名 / 返回类型 / 参数类型 */
String getName();
Class<?> getReturnType();
Class<?>[] getParameterTypes();
```

**典型：setter 注入**

```java
// 注意：方法名一般是 setXxx，不是字段名 name
Method setter = clazz.getDeclaredMethod("setUserDao", UserDao.class);
setter.invoke(bean, userDao);
```

**注意事项**

- 参数 `Class` 必须匹配声明类型；传入实现类实例没问题，但 `getDeclaredMethod` 的第二个参数必须是**声明的形参类型**，不是运行时 `getClass()`（子类 Class 会导致找不到方法）。
  - 反例：`getDeclaredMethod("setDao", userDao.getClass())` — 若形参是接口 `UserDao`，而 `getClass()` 是实现类，会 `NoSuchMethodException`。
  - 正例：`getDeclaredMethod("setDao", UserDao.class)`。
- `getDeclaredMethod` 找不到父类方法；父类 setter 请用 `getMethod` 或递归查父类。
- 可变参数方法在反射里表现为数组类型。
- 桥接方法 / 合成方法：枚举全部方法时注意过滤 `isSynthetic()` / `isBridge()`。

---

## 4. Field — 字段读写

```java
/** public 字段（含父类） */
Field getField(String name);

/** 本类字段（含 private），不含父类 */
Field getDeclaredField(String name);

Field[] getFields();
Field[] getDeclaredFields();

/** 读字段值；静态字段 obj 传 null */
Object get(Object obj);

/** 写字段值；静态字段 obj 传 null */
void set(Object obj, Object value);

void setAccessible(boolean flag);

Class<?> getType();
String getName();
```

**典型：字段注入**

```java
Field field = clazz.getDeclaredField("userDao");
field.setAccessible(true);
field.set(bean, userDao);
```

**注意事项**

- private 字段必须先 `setAccessible(true)`，否则 `IllegalAccessException`。
- `final` 字段在 Java 12+ 通过反射修改限制更严，生产代码勿依赖改 final。
- 基本类型用 `getInt` / `setInt` 等可避免装箱；通用场景用 `get` / `set` 即可。
- 父类字段：`getDeclaredField` 找不到，需沿 `getSuperclass()` 向上查找。

---

## 5. Annotation — 注解元数据

```java
/** 本元素上的注解（需 @Retention(RUNTIME)） */
<A extends Annotation> A getAnnotation(Class<A> annotationClass);

boolean isAnnotationPresent(Class<? extends Annotation> annotationClass);

Annotation[] getAnnotations();          // 含继承下来的（若注解可继承）
Annotation[] getDeclaredAnnotations();  // 仅本元素直接声明
```

**注意事项**

- 注解保留策略必须是 `RUNTIME`，否则反射读不到。
- `@Inherited` 只对**类**上的注解生效，对方法/字段无效。
- 可用在 `Class` / `Method` / `Field` / `Constructor` / `Parameter` 上。

---

## 6. Proxy — JDK 动态代理（进阶）

```java
/**
 * 为接口生成代理实例。
 * @param loader 类加载器
 * @param interfaces 要代理的接口数组
 * @param h InvocationHandler：拦截所有接口方法
 */
static Object Proxy.newProxyInstance(
        ClassLoader loader,
        Class<?>[] interfaces,
        InvocationHandler h);

/**
 * 代理回调。
 * @param proxy 代理对象自身
 * @param method 被调用的接口方法
 * @param args 实参
 */
Object InvocationHandler.invoke(Object proxy, Method method, Object[] args);
```

**注意事项**

- JDK Proxy **只能代理接口**；代理类用 `CGLIB` / ByteBuddy。
- `handler.invoke` 里再调 `method.invoke(proxy, ...)` 会死循环；应调目标对象。
- 返回值类型必须与接口方法兼容，基本类型不能返回 null。

---

## 7. 异常清单（接口契约）

| 异常 | 何时出现 | 处理建议 |
|------|----------|----------|
| `ClassNotFoundException` | `forName` 类名错误 | 检查全限定名与 classpath |
| `NoSuchMethodException` | 方法/构造签名不匹配 | 核对方法名、参数 Class（接口 vs 实现类） |
| `NoSuchFieldException` | 字段名错误或不在本类 | 查父类或改用正确声明类 |
| `IllegalAccessException` | 访问权限不足 | `setAccessible(true)`（注意模块限制） |
| `InstantiationException` | 抽象类/接口/错误实例化 | 检查是否可具体化 |
| `InvocationTargetException` | 被调方法/构造内部抛错 | `e.getCause()` 取根因 |
| `IllegalArgumentException` | 实参类型/个数不对 | 核对 `invoke`/`newInstance` 参数 |
| `InaccessibleObjectException` | Java 9+ 模块封装拒绝深反射 | `--add-opens` 或避免访问 JDK 内部 |

---

## 8. 与 mini-Spring 对照

| 生命周期步骤 | 反射 API | 本项目位置 |
|--------------|----------|------------|
| 实例化 | `getDeclaredConstructor().newInstance()` | `DefaultBeanFactory#instantiate` |
| Setter 注入 | `getDeclaredMethod` + `invoke` | 同上，遍历 `PropertyValue` |
| 字段注入（后续） | `getDeclaredField` + `setAccessible` + `set` | — |
| 初始化回调（后续） | `getMethod("afterPropertiesSet")` + `invoke` | — |
| AOP（后续） | `Proxy.newProxyInstance` | — |

**当前代码易错提醒**

```java
// 风险：用运行时 getClass() 当形参类型，接口注入会失败
aClass.getDeclaredMethod(name, propertyValue.getObject().getClass());

// 更稳妥：BeanDefinition 里保存「声明的参数类型」Class
aClass.getDeclaredMethod(name, propertyValue.getParameterType());
```

---

## 9. 使用清单（Checklist）

- [ ] 用 `getDeclaredConstructor().newInstance()`，不用废弃 API
- [ ] `getDeclaredMethod` 的参数类型用**声明类型**，不用实现类 `getClass()`
- [ ] 访问 private 成员前 `setAccessible(true)`
- [ ] 捕获 `InvocationTargetException` 时打印 / 抛出 `getCause()`
- [ ] 区分 `getXxx`（public+父类）与 `getDeclaredXxx`（本类全部）
- [ ] 基本类型与包装类型 `Class` 不要混用

---

## 10. 最小可运行示例

```java
public class ReflectionDemo {
    public static void main(String[] args) throws Exception {
        Class<?> clazz = UserService.class;

        // 1. 实例化
        Object bean = clazz.getDeclaredConstructor().newInstance();

        // 2. setter 注入
        Method setDao = clazz.getDeclaredMethod("setUserDao", UserDao.class);
        setDao.invoke(bean, new UserDao());

        // 3. 字段读取
        Field field = clazz.getDeclaredField("userDao");
        field.setAccessible(true);
        System.out.println(field.get(bean));
    }
}
```
