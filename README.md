# OkReflect

OkReflect is a library that mean to help use Java Reflect feature with ease.

## Basic Usage
```java
// Java
String str = OkReflect.Companion.on("java.lang.String")
                .create("Hello OkReflect")
                .call("substring", 6)
                .callWithResult("toString")
                .get();
```

```kotlin
val result = OkReflect.on("java.lang.String")
            .create("Hello OkReflect")
            .call("substring", 6)
            .callWithResult("toString")
            .get<String>()
```