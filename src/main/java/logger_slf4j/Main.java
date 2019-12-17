package logger_slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author dataochen
 * @Description
 * 1.如何保证写入效率？内部用的是BIO呀，NIO对于小量数据且不是socket优化力度并不大,AIO由于linux系统并没有良好的支持优化力度也不大
 * @date: 2019/6/6 14:15
 */
public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        System.out.println(1);
        LOGGER.info("dd{}", "s");
        String s = "";
    }
}
