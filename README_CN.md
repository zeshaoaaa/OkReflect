[![](https://jitpack.io/v/zeshaoaaa/OkReflect.svg)](https://jitpack.io/#zeshaoaaa/OkReflect)

# OkReflect

OkReflect 是一个封装了 Java 反射操作的工具。

[OkReflect 介绍](https://www.jianshu.com/p/7ffc22eee6cc)

## 用法

### 1. 使用类名创建类

```java
// Java
String str = OkReflect.on("java.lang.String")
                .create("Hello OkReflect")
                .get();
```

```kotlin
// Kotlin
val str: String? = OkReflect.on("java.lang.String")
            .create("test")
            .get()
```



### 2. 使用 Class 创建类

```java
// Java
String str = OkReflect.on(String.class)
                .create("test")
                .get();
```

```kotlin
// Kotlin
val str: String? = OkReflect.on(String::class.java)
            .create("test")
            .get()
```



### 3. 获取父类的字段值

```java
// Java
TestClass testClass = new TestClass("Alex");
String superName = OkReflect.on(SuperTestClass.class)
  									.with(testClass)
  									.get("superName");
```

```kotlin
// Kotlin
val testClass = TestClass("Alex")
val superName = OkReflect.on(SuperTestClass::class.java)
            .with(testClass)
            .get<String>("superName")
```



### 4. 调用方法

```java
// Java
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .get();
```

```kotlin
// Kotlin
val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)     
            .get<String?>()
```



### 5. 把实例传入 OkReflect 中并调用它的方法或修改它的字段值

```java
// Java
TestClass instance = new TestClass();
String name = OkReflect.onInstance(instance)
                .set("name", "Alex")
                .get("name");
```

```kotlin
// Kotlin
val instance = TestClass()
val name = OkReflect.onInstance(instance)
            .call("getName")
            .get<String>("name")
```



### 6. 使用上一个方法返回的值调用下一个方法

```java
// Java
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .callWithResult("substring", 4)
                .get();
```

```kotlin
// Kotlin
val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .callWithResult("substring", 4)
            .get<String?>()
```



### 7. 忽略方法返回值，直接获取实例

```java
// Java
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .getInstance();
```

```kotlin
// Kotlin
val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .getInstance<String>()
```



### 8. 获取和设置字段的值

```java
// Java
int i = OkReflect.on(TestClass.class)
                .set("i", 6)
                .get("i");
```

```kotlin
// Kotlin
val i = OkReflect.on(TestClass.class)
            .set("i", 6)
            .get<Int?>("i")
```



### 9. 获取和设置静态字段的值

```java
// Java
String finalField = OkReflect.on(TestClass.class)
                .set("staticString", "changed")
                .get("staticString");
```

``` kotlin
// Kotlin
val finalField = OkReflect.on(TestClass.class)
            .set("staticString", "changed")
            .get<String?>("staticString")
```



### 10. 处理异常

```java
// Java
String str = OkReflect
                .on("java.lang.String")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull Exception e) {
                        // 处理异常
                    }
                })
                .get();
```

```kotlin
// Kotlin
val str = OkReflect
            .on("java.lang.String")
            .error {
                // 处理异常
            }
            .get<String>()
```



### 11. 调用方法并传入实参类型信息

```java
// Java
Class classes[] = {String.class, Byte.class};
Object args[] = {"Tom", null};
String name = OkReflect.on(TestClass.class)
                .create()
                .callWithClass("setData2", classes, args)
                .get("name");
```

```kotlin
// Kotlin
val classes = arrayOf<Class<*>>(String::class.java, Byte::class.javaObjectType)
val args = arrayOf<Any?>("Tom", null)
val name = OkReflect.on(TestClass::class.java)
            .create()
            .callWithClass("setData2", classes, *args)
            .get<String?>("name")
```





### 12. 纯设置字段或调用方法

```java
// Java
String name = OkReflect.on(TestClass.class)
                .create()
                .simpleCall("getName");

name = OkReflect.on(TestClass.class)
  							.create()
                .simpleSet("name", "Tom");
```

```kotlin
// Kotlin
var name = OkReflect.on(TestClass::class.java)
            .create()
            .simpleCall<String>("getName")

name = OkReflect.on(TestClass::class.java)
            .create()
            .simpleSet<String>("name", "Tom")
```





### 13. 动态代理

```Java
// 首先声明接口
public interface StringProxy {
  String substring(int beginIndex);
}
```

``` Java
// Java
String substring = OkReflect.on("java.lang.String")
                .create("Hello World")
                .use(StringProxy.class)
                .substring(6);
```

```kotlin
// Kotlin
val substring = OkReflect.on("java.lang.String")
            .create("Hello World")
            .use(StringProxy::class.java)
            .substring(6)
```

### 

## Configuration
### Gradle 
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
```groovy
dependencies {
    implementation 'com.github.zeshaoaaa:OkReflect:master-SNAPSHOT'
}
```
### Maven
```xml
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
```xml
	<dependency>
	    <groupId>com.github.zeshaoaaa</groupId>
	    <artifactId>OkReflect</artifactId>
	    <version>master-SNAPSHOT</version>
	</dependency>
```
