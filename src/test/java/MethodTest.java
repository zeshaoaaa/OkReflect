import okreflect.MethodGetter;
import okreflect.OkReflect;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class MethodTest {

    @Test
    public void testAccessible() {
        try {
            Constructor constructor = TestClass.class.getDeclaredConstructor(String.class, int.class);
            constructor = MethodGetter.Companion.accessible(constructor);
            assert constructor.isAccessible();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
