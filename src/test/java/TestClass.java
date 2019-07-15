public class TestClass extends SuperTestClass {

    private int age;
    private String name = "default";
    private static String staticString = "staticString";
    private final String finalString = "finalString";
    private static final String staticFinalField = "finalString";
    private String nickname = "666";
    private byte b;
    private Byte b2;
    public char c = 'a';
    public Character c2 = 'a';
    private static int i = 10;
    private Integer i2 = 12;

    public TestClass() {

    }

    public TestClass(String name) {
        super(name);
        this.name = name;
    }

    private TestClass(int age) {
        this.name = name;
        this.age = age;
    }

    private TestClass(String name, int age) {
        this.name = name;
        this.age = age;
    }

    private TestClass(String name, Character c) {
        this.name = name;
        this.c2 = c2;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setName2(String name) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private void setData(String name, byte b) {
        this.name = name;
        this.b = b;
    }

    private void setData2(String name, Byte b) {
        this.name = name;
        this.b2 = b;
    }

    private void setData3(String name, Character c) {
        this.nickname = name;
        this.c2 = c;
    }



}
