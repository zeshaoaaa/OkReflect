import okreflect.OkReflect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;


/**
 * This test class responsible for test all the use case.
 */
public class OkReflectTest {

    ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testCreateInstanceByClass() {
        String str = OkReflect.on(String.class)
                .create("test")
                .get();
        Assert.assertEquals(str, "test");
    }

    @Test
    public void testCreateInstanceByClassName() {
        String str = OkReflect.on("TestClass")
                .create("test")
                .get("name");
        Assert.assertEquals(str, "test");
    }


    @Test
    public void testCreateInstanceByPrivateConstructor() {
        Integer age = OkReflect.on(TestClass.class)
                .create(11)
                .get("age");
        assert age == 11;
    }

    @Test
    public void testCreateInstanceWithTwoParameter() {
        String name = OkReflect.on(TestClass.class)
                .create("Tom", 12)
                .get("name");
        Assert.assertEquals(name, "Tom");
    }


    @Test
    public void testSetAndGetFieldFromSuperClass() {
        TestClass testClass = new TestClass();
        String superName = OkReflect.on(SuperTestClass.class)
                .with(testClass)
                .set("superName", "Tom")
                .get("superName");
        assert superName.equals("Tom");
    }


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

    // 10. Handle the exception with async
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
                        assertTrue(e.toString().contains("NoSuchMethod"));
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
        String name = OkReflect.onInstance(testClass)
                .call("getName")
                .get("name");
        assert name.equals("default");
    }

    @Test
    public void testSetFieldFromOuterInstance() {
        TestClass testClass = new TestClass();
        String name = OkReflect.onInstance(testClass)
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
        Class classes[] = {String.class, Byte.class};
        Object args[] = {"Tom", null};
        String name = OkReflect.on(TestClass.class)
                .create()
                .callWithClass("setData2", classes, args)
                .get("name");
        assert name.equals("Tom");
    }

    @Test
    public void testCallMethodsWithVoidParameter() {
        Class classes1[] = {String.class, Byte.class};
        Class classes2[] = {String.class, Character.class};
        Object args1[] = {"Tom", null};
        Object args2[] = {"Bingo", null};
        TestClass instance = OkReflect.on(TestClass.class)
                .create()
                .callWithClass("setData2", classes1, args1)
                .callWithClass("setData3", classes2, args2)
                .get();

        String name = OkReflect.onInstance(instance)
                .get("name");
        String nickname = OkReflect.onInstance(instance)
                .get("nickname");

        assert name.equals("Tom") && nickname.equals("Bingo");
    }

    @Test
    public void testGetResult() {
        TestClass testClass = new TestClass();
        String name = OkReflect.onInstance(testClass)
                .call("getName")
                .getResult();
        assert name.equals("default");
    }

    @Test
    public void testSimpleCall() {
        String name = OkReflect.on(TestClass.class)
                .create()
                .simpleCall("getName");
        assert name.equals("default");
    }

    @Test
    public void testSimpleSet() {
        String name = OkReflect.on(TestClass.class)
                .create()
                .simpleSet("name", "Tom");
        assert name.equals("Tom");
    }


    @Test
    public void testReset() {
        String name1 = OkReflect.on(TestClass.class)
                .create()
                .call("setName", "Tom")
                .get("name");

        String name2 = OkReflect.on(TestClass.class)
                .create()
                .call("setName", "Alex")
                .get("name");

        assert name1.equals("Tom") && name2.equals("Alex");
    }

    @Test
    public void testCallMethodAsync() {
        long start = System.currentTimeMillis();
        OkReflect.on(TestClass.class)
                .create()
                .call("setName2", "Tom")
                .async(result -> {
                    long current = System.currentTimeMillis();
                    long diff = current - start;
                    assertTrue(diff > 2000 && diff < 3000);
                    assertTrue(result instanceof TestClass);
                });
        long diff = System.currentTimeMillis() - start;
        assertTrue(diff < 1000);
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testCallMethodAsyncWithReturnValue() {
        long start = System.currentTimeMillis();
        OkReflect.on(TestClass.class)
                .create()
                .call("setName2", "Tom")
                .field("name")
                .async(result -> {
                    long current = System.currentTimeMillis();
                    long diff = current - start;
                    assertTrue(diff > 2000 && diff < 3000);
                    assertTrue(result instanceof String);
                    assertTrue(result.equals("Tom"));
                });
        long diff = System.currentTimeMillis() - start;
        assertTrue(diff < 1000);
        try {
            Thread.sleep(4 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Ignore
    @Test
    public void testCreateGenericsInstance() {
        GenericsClass genericsClass = OkReflect.on(GenericsClass.class)
                .create("666")
                .get();
        assert genericsClass != null;
    }

    @Ignore
    @Test
    public void testSetFinalFieldOfClass() {
        String finalField = OkReflect.on("TestClass")
                .set("staticFinalField", "changed")
                .get("staticFinalField");
        assert finalField.equals("changed");
    }

    // Added method async() for the purpose of 'Call the method asynchronously'
    // Added method field for the purpose of get the field after async task.

}
