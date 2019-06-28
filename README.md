[中文文档](https://github.com/zeshaoaaa/OkReflect/blob/master/README_CN.md)

# OkReflect
OkReflect is a library that mean to help use Java Reflect feature with ease.

## Usage
```java
// Java
String str = OkReflect.Companion.on("java.lang.String")
                .create("Hello OkReflect")
                .call("substring", 6)
                .callWithResult("toString")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull String errorMsg) {
                        System.out.println(errorMsg);
                    }
                })
                .get();
```

```kotlin
// Kotlin
val result = OkReflect.on("java.lang.String")
            .create("Hello OkReflect")
            .call("substring", 6)
            .callWithResult("toString")
            .error{
                println(it)
            }
            .get<String>()
```

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