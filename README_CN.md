[![](https://jitpack.io/v/zeshaoaaa/OkReflect.svg)](https://jitpack.io/#zeshaoaaa/OkReflect)

# OkReflect

OkReflect 是一个封装了 Java 反射操作的工具。

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
TestClass testClass = new TestClass();
String name = OkReflect.on(testClass)
                .set("name", "Alex")
                .get("name");
```

```kotlin
// Kotlin
val testClass = TestClass()
val name = OkReflect.on(testClass)
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
int i = OkReflect.on("TestClass")
                .set("i", 6)
                .get("i");
```

```kotlin
// Kotlin
val i = OkReflect.on("TestClass")
            .set("i", 6)
            .get<Int?>("i")
```



### 9. 获取和设置静态字段的值

```java
// Java
String finalField = OkReflect.on("TestClass")
                .set("staticString", "changed")
                .get("staticString");
```

``` kotlin
// Kotlin
val finalField = OkReflect.on("TestClass")
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
                        assert e.toString().contains("you have to call create()");
                    }
                })
                .get();
```

```kotlin
// Kotlin
val str = OkReflect
            .on("java.lang.String")
            .error {
                assert(it.toString().contains("you have to call create()"))
            }
            .get<String>()
```



### 11. 动态代理

```Java
// First step of using dynamic proxy: declare the interface
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
