[中文文档](https://github.com/zeshaoaaa/OkReflect/blob/master/README_CN.md)

# OkReflect
OkReflect is a library that mean to help use Java Reflect feature with ease.

## Usage
### Java

```java
// Create instance 
String str = OkReflect.on("java.lang.String")
                .create("Hello OkReflect")
                .get();

// Create instance by class
String str = OkReflect.on(String.class)
                .create("test")
                .get();

// Invoke method with instance
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .get();

// Invoke method with result
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .callWithResult("substring", 4)
                .get();

// Get the instance after invoke the method
 String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .getInstance();

// Handle the exception with callback
String str = OkReflect
                .on("java.lang.String")
                .create("Hello world")
                .call("hhh")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull Exception e) {
                        Assert.assertTrue(e.toString().contains("NoSuchMethod"));
                    }
                })
                .get();

// Dynamic Proxy
String substring = OkReflect.on("java.lang.String")
                .create("Hello World")
                .use(StringProxy.class)
                .substring(6);
```

### Kotlin

```kotlin
// Create instance
val str: String? = OkReflect.on("java.lang.String")
            .create("test")
            .get()

// Create instance by class
val str: String? = OkReflect.on(String::class.java)
            .create("test")
            .get()

// Invoke method
val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .get<String>()

```

### 

## Configuration
```groovy
repositories {
    jcenter()
}
```
```groovy
dependencies {
    implementation 'com.jayou:OkReflect:0.0.1'
}
```