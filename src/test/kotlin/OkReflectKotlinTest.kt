import org.junit.Test

class OkReflectKotlinTest {

    @Test
    fun testCallSubstringAndToString() {
        val result = OkReflect.on("java.lang.String")
            .create("Hello OkReflect")
            .call("substring", 6)
            .callWithResult("toString")
            .error{
                println(it)
            }
            .get<String>()
        println(result)

    }

}