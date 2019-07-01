[中文文档](https://github.com/zeshaoaaa/OkReflect/blob/master/README_CN.md)

# OkReflect
OkReflect is a library that trying to help you use Java Reflect feature with ease.

## Usage
### Java

```java
// Create instance with path of the class
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

// Invoke method with return value of last method.
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
                        // handle the exception
                    }
                })
                .get();

// First step of using dynamic proxy: declare the interface
public interface StringProxy {
  String substring(int beginIndex);
}

// Second step of using dynamic proxy: use
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

// Handle the exception with function
val str = OkReflect
            .on("java.lang.String")
            .error{
                // handle exception
            }
            .get<String>()

// Handle the exception with callback
val str = OkReflect
            .on("java.lang.String")
            .error(object : OkReflect.OkReflectErrorCallback {
                override fun onError(e: Exception) {
                    // handle exception
                }
            })
            .get<String>()

// First step of using dynamic proxy: declare the interface
interface StringProxy {
  fun substring(beginIndex: Int): String
}

// Second step of using dynamic proxy: use
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