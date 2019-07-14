[![](https://jitpack.io/v/zeshaoaaa/OkReflect.svg)](https://jitpack.io/#zeshaoaaa/OkReflect)

[中文文档](https://github.com/zeshaoaaa/OkReflect/blob/master/README_CN.md)

# OkReflect
OkReflect is a library that trying to help you use Java Reflection APIs with ease.

[Introduction to OkReflect](https://medium.com/@zeshao09/okreflect-java-reflection-framework-a86316b919a4/)

## Usage

### 1. Create the instance with the class name

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



### 2. Create the instance by class

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



### 3. Set or get the field value from the superclass

```java
// Java
TestClass testClass = new TestClass("Alex");
String superName = OkReflect.on(SuperTestClass.class)
  									.with(testClass)
  									.set("superName", "Tom")
  									.get("superName");
```

```kotlin
// Kotlin
val testClass = TestClass("Alex")
val superName: String? = OkReflect.on(SuperTestClass::class.java)
            .with(testClass)
			.set("Tom")
            .get("superName")
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
val str: String? = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)     
            .get()
```



### 5. Invoke the methods and set the fields with the instance that created outside of OkReflect

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
val name: String? = OkReflect.on(testClass)
            .call("getName")
            .get("name")
```



### 6. Invoke the method with the return value of the last method

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
val str: String? = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .callWithResult("substring", 4)
            .get()
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
val str: String? = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .getInstance()
```



### 8. Get and set the field value of the instance

```java
// Java
int i = OkReflect.on(TestClass.class)
                .set("i", 6)
                .get("i");
```

```kotlin
// Kotlin
val i: Int? = OkReflect.on(TestClass.class)
            .set("i", 6)
            .get("i")
```



### 9. Get and set the value of the static field of the class

```java
// Java
String finalField = OkReflect.on("TestClass")
                .set("staticString", "changed")
                .get("staticString");
```

``` kotlin
// Kotlin
val finalField: String? = OkReflect.on("TestClass")
            .set("staticString", "changed")
            .get("staticString")
```



### 10. Handle the exception with callback

```java
// Java
String str = OkReflect
                .on("java.lang.String")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull Exception e) {
                        // handle the exception
                    }
                })
                .get();
```

```kotlin
// Kotlin
val str: String? = OkReflect
            .on("java.lang.String")
            .error {
                // handle the exception
            }
            .get()
```



### 11. Call the method with the type of parameters

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
val args = arrayOf("Tom", null)
val name = OkReflect.on(TestClass::class.java)
            .create()
            .callWithClass("setData2", classes, *args)
            .get<String?>("name")
```





### 12. Simply set the field or call the method

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
