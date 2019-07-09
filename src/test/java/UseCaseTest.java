import okreflect.OkReflect;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * This test class responsible for test all the use case.
 */
public class UseCaseTest {

    @Test
    public void testCreateClassWithClassName() {
        String str = OkReflect.on("java.lang.String")
                .create("test")
                .get();
        Assert.assertEquals(str, "test");
    }

    @Test
    public void testCreateInstanceByClass() {
        String str = OkReflect.on(String.class)
                .create("test")
                .get();
        Assert.assertEquals(str, "test");
    }

    @Test
    public void testGetFieldFromSuperClass() {
        TestClass testClass = new TestClass("Alex");
        String superName = OkReflect.on(SuperTestClass.class)
                .with(testClass)
                .get("superName");
        assert superName.equals("Alex");
    }

    @Test
    public void testCreateInstanceByPrivateConstructor() {
        String name = OkReflect.on("TestClass")
                .create("Tom", 11)
                .get("name");
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

    // 6. Invoke method with return value of last method
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

    // 10. Handle the exception with callback
    @Test
    public void testNotCallCreateErrorCallback() {
        String str = OkReflect
                .on("java.lang.String")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull Exception e) {
                        assert e.toString().contains("you have to call create()");
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


    // 8. Get and set field value of instance
    @Test
    public void testSetAndGetStaticField() {
        int i = OkReflect.on("TestClass")
                .set("i", 6)
                .get("i");
        assert i == 6;
    }

    @Test
    public void testSetFinalFieldOfInstance() {
        String finalField = OkReflect.on("TestClass")
                .create()
                .set("finalString", "changed")
                .get("finalString");
        assert finalField.equals("changed");
    }


    @Test
    public void testGetClass() {
        Class clazz = OkReflect.on("TestClass")
                .getClazz();
        assert clazz.equals(TestClass.class);
    }

    // 9. Get and set static field value of the class
    @Test
    public void testSetStaticField() {
        String finalField = OkReflect.on("TestClass")
                .set("staticString", "changed")
                .get("staticString");
        assert finalField.equals("changed");
    }

    @Test
    public void testSetStaticFinalFieldOfInstance() {
        String finalField = OkReflect.on("TestClass")
                .create()
                .set("staticFinalField", "changed")
                .get("staticFinalField");
        assert finalField.equals("changed");
    }

    @Test
    public void testSetStaticFinalFieldOfClass() {
        String finalField = OkReflect.on("TestClass")
                .set("staticFinalField", "changed")
                .get("staticFinalField");
        assert finalField.equals("changed");
    }

    // 5. Invoke methods and set fields with instance that created outside of OkReflect
    @Test
    public void testCallMethodFromOuterInstance() {
        TestClass testClass = new TestClass();
        String name = OkReflect.on(testClass)
                .call("getName")
                .get("name");
        assert name.equals("default");
    }

    @Test
    public void testSetFieldFromOuterInstance() {
        TestClass testClass = new TestClass();
        String name = OkReflect.on(testClass)
                .set("name", "Alex")
                .get("name");
        assert name.equals("Alex");
    }

    @Test
    public void testCallMethodWithMultipleParameter() {
        byte b = 1;
        Object[] p = {"Tom", b};
        String name = OkReflect.on(TestClass.class)
                .create()
                .call("setData", p)
                .get("name");
        assert name.equals("Tom");
    }

    @Test
    public void testCallMethodWithVoidParameter() {
        TestClass testClass = new TestClass();
        Class classes[] = {String.class, Byte.class};
        String name = OkReflect.on(testClass)
                .call("setData2", classes, "Tom", null)
                .get("name");
        assert name.equals("Tom");
    }

    @Test
    public void testGetResult() {
        TestClass testClass = new TestClass();
        String name = OkReflect.on(testClass)
                .call("getName")
                .getResult();
        assert name.equals("default");
    }

    @Ignore
    @Test
    public void testSetFinalFieldOfClass() {
        String finalField = OkReflect.on("TestClass")
                .set("staticFinalField", "changed")
                .get("staticFinalField");
        assert finalField.equals("changed");
    }

    // Fix Bug 'set field of instance failed, when class is super class',
    // Fix Bug 'get class of parameter failed, when parameter is null'
    // Added getResult() method for the purpose of obtain the return value no matter it is null or not.
    // Added classes parameter to call() and simpleCall() methods for the purpose of
    // passing void parameter into the method.

}
