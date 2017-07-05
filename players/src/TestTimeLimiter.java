import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;

import java.util.concurrent.TimeUnit;

/**
 * TODO documentation
 */
public class TestTimeLimiter {
    public static void main(String[] args) {
        SimpleTimeLimiter timeLimiter = new SimpleTimeLimiter();
        long startTime = 0;
        try {
            TestProxy proxy = timeLimiter.newProxy(
                    new TestProxyImpl(), TestProxy.class, 300,
                    TimeUnit.MILLISECONDS);
            startTime = System.currentTimeMillis();
            proxy.test();
        } catch (UncheckedTimeoutException e) {
            System.out.println("Timeour");
            // The requirement is to create an artificial delay = average response time
        }
        System.out.println("TimeSpent : " + (System.currentTimeMillis() - startTime));
    }
}
