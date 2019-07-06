import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class OkReflectTest {


    @Test
    public void testAccessible() {
        try {
            Constructor constructor = TestClass.class.getDeclaredConstructor(String.class, int.class);
            constructor = OkReflect.Companion.accessible(constructor);
            assert constructor.isAccessible();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

}
