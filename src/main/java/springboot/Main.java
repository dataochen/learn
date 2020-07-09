package springboot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * @author dataochen
 * @Description
 * @date: 2020/7/9 16:50
 */
@Configuration
public class Main {
    public static void main(String[] args) {
    }
    @Bean
    @Profile("test")
    public TestBean testBean() {
        TestBean testBean = new TestBean();
        return testBean;
    }
}
