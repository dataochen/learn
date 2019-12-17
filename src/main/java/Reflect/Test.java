package Reflect;

/**
 * @author dataochen
 * @Description
 * @date: 2019/6/5 21:32
 */
public class Test {
    public Test() {
    }

    public Test(String pri, String pub) {
        this.pri = pri;
        this.pub = pub;
    }

    private String pri;
    public String pub;
    private String test(String param) {
        return param;
    }
}
