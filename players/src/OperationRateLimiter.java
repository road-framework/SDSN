import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO documentation
 */
public class OperationRateLimiter {

    private String opName;
    private ROADProperties roadProperties;
    private long windowLength = 2 * 60 * 1000;
    private int threshold = Integer.MAX_VALUE;
    private long averageResponseTime = 5 * 1000;
    private AtomicInteger rate = new AtomicInteger(0);
    private TimeLimiter limiter;
    private ROADThreadPool roadThreadPool;
    private final Lock lock = new ReentrantLock();

    public OperationRateLimiter(String serviceName, String opName, ROADProperties roadProperties) {

        this.opName = opName;
        this.roadProperties = roadProperties;
        windowLength = Long.parseLong(roadProperties.getProperty(serviceName + "." + opName + ".window", String.valueOf(windowLength)));
        averageResponseTime = Long.parseLong(roadProperties.getProperty(serviceName + "." + opName + ".resTime", String.valueOf(averageResponseTime)));
        threshold = Integer.parseInt(roadProperties.getProperty(serviceName + "." + opName + ".threshold", String.valueOf(threshold)));
        roadThreadPool = ROADThreadPoolFactory.createROADThreadPool(opName, roadProperties);
        limiter = new SimpleTimeLimiter(roadThreadPool);
        rate.set(threshold);
    }

    public long getAverageResponseTime() {
        return averageResponseTime;
    }

    public TimeLimiter getLimiter() {
        return limiter;
    }

    public long getWindowLength() {
        return windowLength;
    }

    public int getThreshold() {
        return threshold;
    }

    public boolean tryConsume() {
        return true; // TODO  just for test - correctly fix as below
//        lock.lock();
//        try {
//            if ((rate.get() - 1) < 0) {
//                return false;
//            } else {
//                rate.decrementAndGet();
//                return true;
//            }
//        } finally {
//            lock.unlock();
//        }
    }

    public void refill() {
        // TODO  just for test - correctly fix as below
//        lock.lock();
//        try {
//            rate.incrementAndGet();
//        } finally {
//            lock.unlock();
//        }
    }
}
