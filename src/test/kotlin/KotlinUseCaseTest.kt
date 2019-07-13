import okreflect.OkReflect
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.rules.ExpectedException

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method


/**
 * This test class responsible for test all the use case.
 */
class KotlinUseCaseTest {

    @Test
    fun testCreateClassWithClassName() {
        val str = OkReflect.on("java.lang.String")
            .create("test")
            .get<String>()
        Assert.assertEquals(str, "test")
    }

    @Test
    fun testCreateInstanceByClass() {
        val str = OkReflect.on(String::class.java)
            .create("test")
            .get<String>()
        Assert.assertEquals(str, "test")
    }

    @Test
    fun testSetAndGetFieldFromSuperClass() {
        val testClass = TestClass()
        val superName = OkReflect.on(SuperTestClass::class.java)
            .with(testClass)
            .set("superName", "Tom")
            .get<String>("superName")
        assert(superName == "Tom")
    }

    @Test
    fun testCreateInstanceByPrivateConstructor() {
        val name = OkReflect.on("TestClass")
            .create("Tom", 11)
            .get<String>("name")
        Assert.assertTrue(name == "Tom")
    }

    internal var expectedException = ExpectedException.none()

    @Test(expected = NullPointerException::class)
    fun testNotCallCreateException() {
        val str = OkReflect
            .on("java.lang.String")
            .get<String>()
        expectedException.expectMessage("you have to call")
    }

    @Test
    fun testCallMethod() {
        val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .get<String>()
        Assert.assertEquals(str, "world")
    }

    // 6. Invoke method with return value of last method
    @Test
    fun testCallWithResult() {
        val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .callWithResult("substring", 4)
            .get<String>()
        Assert.assertEquals(str, "d")
    }

    @Test
    fun testGetInstanceAfterMethodCall() {
        val str = OkReflect
            .on(String::class.java)
            .create("Hello world")
            .call("substring", 6)
            .getInstance<String>()
        Assert.assertEquals(str, "Hello world")
    }

    // 10. Handle the exception with callback
    @Test
    fun testNotCallCreateErrorCallback() {
        val str = OkReflect
            .on("java.lang.String")
            .error(object : OkReflect.OkReflectErrorCallback {
                override fun onError(e: Exception) {
                    assert(e.toString().contains("you have to call create()"))
                }
            })
            .get<String>()
    }

    @Test
    fun testNoSuchMethodErrorCallback() {
        val str = OkReflect
            .on("java.lang.String")
            .create("Hello world")
            .call("hhh")
            .error(object : OkReflect.OkReflectErrorCallback {
                override fun onError(e: Exception) {
                    Assert.assertTrue(e.toString().contains("NoSuchMethod"))
                }
            })
            .get<String>()
    }

    /**
     * Test call private method that the parameter is  primitive type.
     */
    @Test
    fun testCallPrivatePrimitiveTypeParameterMethod() {
        val b: Byte = 1
        val result = OkReflect.on("TestClass")
            .create("Tom")
            .call("setByte", b)
            .call("getByte")
            .get<Byte>()!!
        Assert.assertEquals(result.toLong(), 1)
    }

    interface StringProxy {
        fun substring(beginIndex: Int): String
    }

    @Test
    fun testDynamicProxy() {
        val substring = OkReflect.on("java.lang.String")
            .create("Hello World")
            .use(StringProxy::class.java)
            .substring(6)
        Assert.assertEquals(substring, "World")
    }

    @Test
    fun testStringToJavaFile() {
    }


    // 8. Get and set field value of instance
    @Test
    fun testSetAndGetStaticField() {
        val i = OkReflect.on("TestClass")
            .set("i", 6)
            .get<Int>("i")!!
        assert(i == 6)
    }

    @Test
    fun testSetFinalFieldOfInstance() {
        val finalField = OkReflect.on("TestClass")
            .create()
            .set("finalString", "changed")
            .get<String>("finalString")
        assert(finalField == "changed")
    }


    @Test
    fun testGetClass() {
        val clazz = OkReflect.on("TestClass")
            .getClazz()
        assert(clazz == TestClass::class.java)
    }

    // 9. Get and set static field value of the class
    @Test
    fun testSetStaticField() {
        val finalField = OkReflect.on("TestClass")
            .set("staticString", "changed")
            .get<String>("staticString")
        assert(finalField == "changed")
    }

    @Test
    fun testSetStaticFinalFieldOfInstance() {
        val finalField = OkReflect.on("TestClass")
            .create()
            .set("staticFinalField", "changed")
            .get<String>("staticFinalField")
        assert(finalField == "changed")
    }

    @Test
    fun testSetStaticFinalFieldOfClass() {
        val finalField = OkReflect.on("TestClass")
            .set("staticFinalField", "changed")
            .get<String>("staticFinalField")
        assert(finalField == "changed")
    }

    // 5. Invoke methods and set fields with instance that created outside of OkReflect
    @Test
    fun testCallMethodFromOuterInstance() {
        val testClass = TestClass()
        val name = OkReflect.onInstance(testClass)
            .call("getName")
            .get<String>("name")
        assert(name == "default")
    }

    @Test
    fun testSetFieldFromOuterInstance() {
        val testClass = TestClass()
        val name = OkReflect.onInstance(testClass)
            .set("name", "Alex")
            .get<String>("name")
        assert(name == "Alex")
    }

    @Test
    fun testCallMethodWithMultipleParameter() {
        val b: Byte = 1
        val p = arrayOf<Any>("Tom", b)
        val name = OkReflect.on(TestClass::class.java)
            .create()
            .call("setData", *p)
            .get<String>("name")
        assert(name == "Tom")
    }

    @Test
    fun testCallMethodWithVoidParameter() {
        val classes = arrayOf<Class<*>>(String::class.java, Byte::class.javaObjectType)
        val args = arrayOf<Any?>("Tom", null)
        val name = OkReflect.on(TestClass::class.java)
            .create()
            .callWithClass("setData2", classes, *args)
            .get<String?>("name")
        assert(name == "Tom")
    }

    @Test
    fun testCallMethodsWithVoidParameter() {
        val classes1 = arrayOf<Class<*>>(String::class.java, Byte::class.javaObjectType)
        val classes2 = arrayOf<Class<*>>(String::class.java, Char::class.javaObjectType)
        val args1 = arrayOf<Any?>("Tom", null)
        val args2 = arrayOf<Any?>("Bingo", null)
        val instance = OkReflect.on(TestClass::class.java)
            .create()
            .callWithClass("setData2", classes1, *args1)
            .callWithClass("setData3", classes2, *args2)
            .get<TestClass>()

        val name = OkReflect.onInstance(instance!!)
            .get<String>("name")
        val nickname = OkReflect.onInstance(instance!!)
            .get<String>("nickname")

        assert(name == "Tom" && nickname == "Bingo")
    }

    @Test
    fun testGetResult() {
        val testClass = TestClass()
        val name = OkReflect.onInstance(testClass)
            .call("getName")
            .getResult<String>()
        assert(name == "default")
    }

    @Test
    fun testSimpleCall() {
        val name = OkReflect.on(TestClass::class.java)
            .create()
            .simpleCall<String>("getName")
        assert(name == "default")
    }

    @Test
    fun testSimpleSet() {
        val name = OkReflect.on(TestClass::class.java)
            .create()
            .simpleSet<String>("name", "Tom")
        assert(name == "Tom")
    }

    @Ignore
    @Test
    fun testSetFinalFieldOfClass() {
        val finalField = OkReflect.on("TestClass")
            .set("staticFinalField", "changed")
            .get<String>("staticFinalField")
        assert(finalField == "changed")
    }

    @Ignore
    @Test
    fun testChangeParameterTypeByMethodHandle() {
        val originTypes = arrayOf<Class<*>>(String::class.java, Char::class.java)
        val newTypes = arrayOf<Class<*>>(String::class.java, Char::class.java)
        try {
            val method = TestClass::class.java.getDeclaredMethod("setData3", *originTypes)
            method.isAccessible = true
            val methodHandle = MethodHandles.lookup().unreflect(method)
            val filter = getFilter(originTypes, newTypes)
            MethodHandles.filterArguments(methodHandle, 2, filter)
            println("")
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    @Throws(IllegalAccessException::class, NoSuchMethodException::class)
    private fun getFilter(originTypes: Array<Class<*>>, newTypes: Array<Class<*>>): MethodHandle {
        var filterMethod: Method? = TestClass::class.java.getDeclaredMethod("setData3", *originTypes)
        filterMethod = OkReflect.onInstance(filterMethod!!)
            .set("parameterTypes", newTypes)
            .get<Method>()
        filterMethod!!.isAccessible = true
        return MethodHandles.lookup().unreflect(filterMethod)
    }


}
