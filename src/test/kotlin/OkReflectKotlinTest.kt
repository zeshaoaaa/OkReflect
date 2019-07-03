import com.sun.source.tree.AssertTree
import org.junit.Assert
import org.junit.Test
import org.junit.rules.ExpectedException
import java.lang.NullPointerException
import kotlin.math.exp

class OkReflectKotlinTest {

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
    fun testHandleExceptionWithFunction() {
        val str = OkReflect
            .on("java.lang.String")
            .error {
                Assert.assertTrue(it.toString().contains("you have to call create()"))
            }
            .get<String>()
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
    fun testSetField() {
        val value = OkReflect.on("java.lang.String")
            .create()
            .set("value", "Alex".toCharArray())
            .getField<CharArray>("value")
        println("")
    }

    @Test
    fun testSetAndGetStaticField() {
        val staticField: Int? = OkReflect.on("TestClass")
            .set("staticField", 6)
            .getField("staticField")
        println("")
    }

    @Test
    fun testStringToJavaFile() {
    }

}