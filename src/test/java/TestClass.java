public class TestClass {

    private int age;
    private String name = "default";
    private final String nickname = "666";
    private byte b;
    public char c = 'a';
    private static int i = 10;
    private  Integer i2 = 12;

    public TestClass() {

    }

    public TestClass(String name) {
        this.name = name;
    }

    private TestClass(String name, int age) {
        this.name = name;
        this.age = age;
    }

    private String getName() {
        return name;
    }

    private void setByte(byte b) {
        this.b = b;
    }

    private byte getByte() {
        return b;
    }

}
