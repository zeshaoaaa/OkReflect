import okreflect.OkReflect;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Ignore;
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
                .get("i");
        assert i == 6;
        System.out.println("");
    }

    @Test
    public void testOriginalSetFinalField() {
        Field finalField = null;
        try {
            TestClass testClass = new TestClass();
            Class clazz = testClass.getClass();
            finalField = clazz.getDeclaredField("finalString");
            finalField.setAccessible(true);
            finalField.set(testClass, "changed");
            String result = (String) finalField.get(testClass);
            assert result.equals("changed");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
    public void testOriginCallMethodWithMultipleParameter() {
        byte b = 1;
        Object[] p = {"Tom", b};
        Field finalField = null;
        try {
            TestClass testClass = new TestClass();
            Class clazz = testClass.getClass();
            Class classArray[] = {String.class, byte.class};
            Method method = clazz.getDeclaredMethod("setData", classArray);
            finalField = clazz.getDeclaredField("finalString");
            finalField.setAccessible(true);
            finalField.set(testClass, "changed");
            String result = (String) finalField.get(testClass);
            assert result.equals("changed");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCallMethodWithMultipleParameter() {
        byte b = 1;
        Object[] p = {"Tom", b};
        String name = OkReflect.on(TestClass.class)
                .create()
                .call("setData", p)
                .get();
        assert name.equals("Tom");
    }

    @Ignore
    @Test
    public void testSetFinalFieldOfClass() {
        String finalField = OkReflect.on("TestClass")
                .set("finalString", "changed")
                .get("finalString");
        assert finalField.equals("changed");
    }


}
