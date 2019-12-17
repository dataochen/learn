package SPI;

/**
 * @author dataochen
 * @Description
 * @date: 2019/6/5 18:08
 */
public class TestOneImpl implements TestInterface {
    @Override
    public String getTest(String param) {
        return "one";
    }
}
