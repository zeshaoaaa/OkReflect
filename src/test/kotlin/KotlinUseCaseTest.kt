import com.sun.source.tree.AssertTree
import okreflect.OkReflect
import org.junit.Assert
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.NullPointerException
import java.lang.reflect.Field
import kotlin.math.exp

class KotlinUseCaseTest {

    // Test whether OkReflect can create instance with constructor that have parameter.
    @Test
    fun testCreateInstanceFromClassNameWithConstructorThatHaveParameter() {
        val str = OkReflect.on("java.lang.String")
            .create("test")
            .get<String>()
        Assert.assertEquals(str, "test")
    }

    @Test
    fun testCreateInstanceFromClass() {
        val str = OkReflect.on(String::class.java)
            .create("test")
            .get<String>()
        Assert.assertEquals(str, "test")
    }

    @Test
    fun testCreateInstanceByPrivateConstructor() {
        val name = OkReflect.on("TestClass")
            .create("Tom", 11)
            .get<String>("name")

        Assert.assertTrue(name == "Tom")
    }

    private var expectedException = ExpectedException.none()

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
            .error(object : OkReflect.OkReflectErrorCallback {
                override fun onError(e: Exception) {
                    Assert.assertTrue(e.toString().contains("you have to call create()"))
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

    @Test
    fun testSetAndGetStaticField() {
        val i = OkReflect.on("TestClass")
            .set("i", 6)
            .get<Int>("i")!!
        assert(i == 6)
        println("")
    }

    @Test
    fun testOriginalSetFinalField() {
        /*String finalField = OkReflect.on("TestClass")
                .set("finalString", "changed")
                .getField("finalString");*/
        var finalField: Field? = null
        try {
            val testClass = TestClass()
            val clazz = testClass.javaClass
            finalField = clazz.getDeclaredField("finalString")
            finalField!!.isAccessible = true
            finalField.set(testClass, "changed")
            val result = finalField.get(testClass) as String
            assert(result == "changed")
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }

    }

    @Test
    fun testSetFinalFieldByOkReflect() {
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
    fun testSetStaticFinalField() {
        val finalField = OkReflect.on("TestClass")
            .create()
            .set("staticFinalField", "changed")
            .get<String>("staticFinalField")
        assert(finalField == "changed")
    }


}