import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * This test class responsible for test all the use case.
 */
public class UseCaseTest {

    // Test whether OkReflect can create instance with constructor that have parameter.
    @Test
    public void testCreateInstanceFromClassNameWithConstructorThatHaveParameter() {
        String str = OkReflect.on("java.lang.String")
                .create("test")
                .get();
        Assert.assertEquals(str, "test");
    }

    @Test
    public void testCreateInstanceFromClass() {
        String str = OkReflect.on(String.class)
                .create("test")
                .get();
        Assert.assertEquals(str, "test");
    }

    @Test
    public void testCreateInstanceByPrivateConstructor() {
        String name = OkReflect.on("TestClass")
                .create("Tom", 11)
                .getField("name");

        Assert.assertTrue(name.equals("Tom"));
    }

    ExpectedException expectedException = ExpectedException.none();

    @Test(expected = NullPointerException.class)
    public void testNotCallCreateException() {
        String str = OkReflect
                .on("java.lang.String")
                .get();
        expectedException.expectMessage("you have to call");
    }

    @Test
    public void testCallMethod() {
        String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .get();
        Assert.assertEquals(str, "world");
    }

    @Test
    public void testCallWithResult() {
        String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .callWithResult("substring", 4)
                .get();
        Assert.assertEquals(str, "d");
    }

    @Test
    public void testGetInstanceAfterMethodCall() {
        String str = OkReflect
                .on(String.class)
                .create("Hello world")
                .call("substring", 6)
                .getInstance();
        Assert.assertEquals(str, "Hello world");
    }

    @Test
    public void testNotCallCreateErrorCallback() {
        String str = OkReflect
                .on("java.lang.String")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull Exception e) {
                        Assert.assertTrue(e.toString().contains("you have to call create()"));
                    }
                })
                .get();
    }

    @Test
    public void testNoSuchMethodErrorCallback() {
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
    }

    /**
     * Test call private method that the parameter is  primitive type.
     */
    @Test
    public void testCallPrivatePrimitiveTypeParameterMethod() {
        byte b = 1;
        byte result = OkReflect.on("TestClass")
                .create("Tom")
                .call("setByte", b)
                .call("getByte")
                .get();
        Assert.assertEquals(result, 1);
    }

    public interface StringProxy {
        String substring(int beginIndex);
    }

    @Test
    public void testDynamicProxy() {
        String substring = OkReflect.on("java.lang.String")
                .create("Hello World")
                .use(StringProxy.class)
                .substring(6);
        Assert.assertEquals(substring, "World");
    }

    @Test
    public void testStringToJavaFile() {
    }

    @Test
    public void testSetAndGetStaticField() {
        int i = OkReflect.on("TestClass")
                .set("i", 6)
                .getField("i");
        assert i == 6;
        System.out.println("");
    }

}
