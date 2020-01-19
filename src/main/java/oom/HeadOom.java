package oom;

import java.util.LinkedList;

/**
 * @author dataochen
 * @Description
 * @date: 2020/1/19 18:08
 */
public class HeadOom {
    public static void main(String[] args) {
        LinkedList<Object> objects = new LinkedList<>();
        while (true) {
            Inner inner = new Inner();
            objects.add(inner);
        }
    }

  static   class Inner{

    }
}
