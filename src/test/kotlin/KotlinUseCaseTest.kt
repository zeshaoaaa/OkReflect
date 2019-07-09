import okreflect.OkReflect
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.rules.ExpectedException

import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Field
import java.lang.reflect.Method


/**
 * This test class responsible for test all the use case.
 */
class KotlinUseCaseTest {

    internal var expectedException = ExpectedException.none()

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
    fun testGetFieldFromSuperClass() {
        val testClass = TestClass("Alex")
        val superName = OkReflect.on(SuperTestClass::class.java)
            .with(testClass)
            .get<String>("superName")
        assert(superName == "Alex")
    }

    @Test
    fun testCreateInstanceByPrivateConstructor() {
        val name = OkReflect.on("TestClass")
            .create("Tom", 11)
            .get<String>("name")
        Assert.assertTrue(name == "Tom")
    }

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

    @Test
    fun testNotCallCreateErrorCallback() {
        val str = OkReflect
            .on("java.lang.String")
            .error {
                assert(it.toString().contains("you have to call create()"))
            }
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
        val name = OkReflect.on(testClass)
            .call("getName")
            .get<String>("name")
        assert(name == "default")
    }

    @Test
    fun testSetFieldFromOuterInstance() {
        val testClass = TestClass()
        val name = OkReflect.on(testClass)
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


    @Ignore
    @Test
    fun testSetFinalFieldOfClass() {
        val finalField = OkReflect.on("TestClass")
            .set("staticFinalField", "changed")
            .get<String>("staticFinalField")
        assert(finalField == "changed")
    }


}
