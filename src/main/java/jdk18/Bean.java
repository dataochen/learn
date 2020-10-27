package jdk18;

/**
 * @author dataochen
 * @Description
 * @date: 2020/10/26 16:13
 */
public class Bean {
    private String a;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"a\":\"")
                .append(a).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
