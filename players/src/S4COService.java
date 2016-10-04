//import javax.jws.WebService;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import org.apache.axis2.AxisFault;
import org.isomorphism.util.TokenBucket;
import org.isomorphism.util.TokenBuckets;

import java.util.concurrent.TimeUnit;

//@WebService(name="S4CO", targetNamespace="http://ws.apache.org/axis2", serviceName="S4COService")
public class S4COService {

    private long windowLength = 2 * 60 * 1000;
    private int threshold = 5;
    private long averageResponseTime = 20 * 1000;
    private TokenBucket bucket;
    private TimeLimiter limiter;
    private ROADThreadPool roadThreadPool;

    public S4COService() {
        bucket = TokenBuckets.builder()
                .withCapacity(threshold)
                .withFixedIntervalRefillStrategy(threshold, windowLength, TimeUnit.MILLISECONDS)
                .build();
        roadThreadPool = ROADThreadPoolFactory.createROADThreadPool("S4COService", ROADProperties.getInstance("cos1.properties"));
        limiter = new SimpleTimeLimiter(roadThreadPool);
    }

    public String analyze(String content) throws AxisFault {
        if (!bucket.tryConsume(1)) {
            throw new AxisFault("Capacity limit has reached");
        }
        TargetType proxy = limiter.newProxy(
                new TargetTypeImpl(), TargetType.class, averageResponseTime, TimeUnit.MILLISECONDS);
        try {
            return proxy.analyse(content, averageResponseTime);
        } catch (UncheckedTimeoutException e) {
            return content;
        }
    }

    public String notify(String content) {
        String res = " Car is towed to. Southlink Smash Repairs - Dandenong";
        System.out.println(res);
        return res;
    }
}