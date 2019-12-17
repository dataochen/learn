package SPI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author dataochen
 * @Description
 * @date: 2019/6/5 20:21
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ServiceLoader<TestInterface> testInterfaces = ServiceLoader.load(TestInterface.class);
        Iterator<TestInterface> iterator = testInterfaces.iterator();

        while (iterator.hasNext()) {
            TestInterface next = iterator.next();
            String test = next.getTest("");
            System.out.println(test);
        }
    }
}
