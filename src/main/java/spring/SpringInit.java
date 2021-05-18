package spring;

import org.springframework.context.Lifecycle;

/**
 * spring容器开始监听
 * @author chendatao
 * @since 1.0
 */
public class SpringInit implements Lifecycle {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }
}
