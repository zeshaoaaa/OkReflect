# OkReflect

OkReflect 是一个封装了 Java 反射操作的工具。

## 用法
### Java

```java
// 使用类名创建实例
String str = OkReflect.on("java.lang.String")
                .create("Hello OkReflect")
                .get();

// 使用 Class 创建实例
String str = OkReflect.on(String.class)
                .create("test")
                .get();

// 使用实例调用方法
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .get();

// 使用上个方法返回的结果调用方法
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .callWithResult("substring", 4)
                .get();

// 在调用完方法后获取实例
String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .getInstance();

// 使用回调处理异常
String str = OkReflect
                .on("java.lang.String")
                .create("Hello world")
                .call("hhh")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull Exception e) {
                        // 处理异常
                    }
                })
                .get();

// 动态代理第一步：声明接口
public interface StringProxy {
  String substring(int beginIndex);
}

// 动态代理第二步：使用 use 方法
String substring = OkReflect.on("java.lang.String")
                .create("Hello World")
                .use(StringProxy.class)
                .substring(6);
```

### Kotlin

```kotlin
// 使用类名创建实例
val str: String? = OkReflect.on("java.lang.String")
            .create("test")
            .get()

// 使用 Class 创建实例
val str: String? = OkReflect.on(String::class.java)
            .create("test")
            .get()

// 使用实例调用方法
val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .get<String>()

// 使用上个方法返回的结果调用方法
val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .callWithResult("substring", 4)
            .get<String>()

// 使用回调处理异常
val str = OkReflect
            .on("java.lang.String")
            .error(object : OkReflect.OkReflectErrorCallback {
                override fun onError(e: Exception) {
                    // 处理异常
                }
            })
            .get<String>()

// 动态代理第一步：声明接口
interface StringProxy {
  fun substring(beginIndex: Int): String
}

// 动态代理第二步：使用 use 方法
val substring = OkReflect.on("java.lang.String")
            .create("Hello World")
            .use(StringProxy::class.java)
            .substring(6)



```

### 

## Configuration
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
    implementation 'com.github.zeshaoaaa:OkReflect:Tag'
}
```