[![](https://jitpack.io/v/zeshaoaaa/OkReflect.svg)](https://jitpack.io/#zeshaoaaa/OkReflect)

[中文文档](https://github.com/zeshaoaaa/OkReflect/blob/master/README_CN.md)

# OkReflect
OkReflect is a library that trying to help you use Java Reflection APIs with ease.

[Introduction of OkReflect](https://medium.com/@zeshao09/okreflect-java-reflection-framework-a86316b919a4/)

## Usage

### 1. Create instance with class name

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



### 2. Create instance by class

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



### 3. Get the field value from the super class

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



### 4. Invoke the method

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



### 5. Invoke methods and set fields with instance that created outside of OkReflect

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



### 6. Invoke method with return value of last method

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



### 7. Get the instance after invoke the method

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



### 8. Get and set field value of instance

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



### 9. Get and set static field value of the class

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



### 10. Handle the exception with callback

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



### 11. Call method with class info

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





### 12. Simply set field or call method

```java
// Java
TestClass testClass = new TestClass();
String nameFromeMethod = OkReflect.on(testClass)
                .simpleCall("getName");

String name = OkReflect.on(testClass)
                .simpleSet("name", "Tom");
```

```kotlin
// Kotlin
val testClass = TestClass()
val nameFromMethod = OkReflect.on(testClass)
            .simpleCall<String>("getName")

val name = OkReflect.on(testClass)
            .simpleSet<String>("name", "Tom")
```



### 13. Use dynamic proxy

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
