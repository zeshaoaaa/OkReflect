public class TestClass {

    private String name = "default";
    private final String nickname = "666";
    private byte b;
    public char c = 'a';

    public TestClass() {

    }

    public TestClass(String name) {
        this.name = name;
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
