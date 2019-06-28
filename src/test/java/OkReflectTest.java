import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

public class OkReflectTest {

    @Test
    public void testCallPrivatePrimitiveTypeParameterMethod() {
        byte b = 1;
        byte result = OkReflect.Companion.on("TestClass")
                .create("Tom")
                .call("setByte", b)
                .call("getByte")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull String errorMsg) {
                        System.out.println(errorMsg);
                    }
                })
                .get();
        Assert.assertEquals(result, 1);
    }

    @Test
    public void testCallSubstringAndToString() {
        String str = OkReflect.Companion.on("java.lang.String")
                .create("Hello OkReflect")
                .call("substring", 6)
                .callWithResult("toString")
                .error(new OkReflect.OkReflectErrorCallback() {
                    @Override
                    public void onError(@NotNull String errorMsg) {
                        System.out.println(errorMsg);
                    }
                })
                .get();
        System.out.println(str);

    }

}
