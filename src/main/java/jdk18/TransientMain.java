package jdk18;

import com.alibaba.fastjson.JSONObject;

/**
 * @author dataochen
 * @Description
 * @date: 2020/10/26 11:04
 */
public class TransientMain {
    private String a;
    private transient String b;
    private transient static String c;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public static void setC(String c) {
        TransientMain.c = c;
    }

    public static void main(String[] args) {
        TransientMain transientMain = new TransientMain();
        TransientMain transientMain2 = new TransientMain();
        transientMain.setA("a");
        transientMain.setB("b");
        TransientMain.c = "c";
        transientMain2.setB("b2");
        System.out.println(JSONObject.toJSONString(transientMain));
        System.out.println(JSONObject.toJSONString(transientMain2));
    }

}
