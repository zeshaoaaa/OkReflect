public class TestClass {

    private final String name;
    private byte b;

    public TestClass(String name) {
        this.name = name;
    }

    private void setByte(byte b) {
        this.b = b;
    }

    private byte getByte() {
        return b;
    }

}
