# OkReflect

OkReflect 是一个封装了 Java 反射操作的工具。

## 用法
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


## 配置
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