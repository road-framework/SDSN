package workload;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO documentation
 */

public class TenantExecutor extends ThreadPoolExecutor {

    public static final String ROAD_THREAD_GROUP = "tenant-thread-group";
    public static final String ROAD_THREAD_ID_PREFIX = "Worker";

    public TenantExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                          TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                new TenantThreadFactory(
                        new ThreadGroup(ROAD_THREAD_GROUP), ROAD_THREAD_ID_PREFIX));
    }

    public TenantExecutor(int corePoolSize, int maxPoolSize, long keepAliveTime, int qlen,
                          String threadGroup, String threadIdPrefix) {
        super(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
                qlen > 0 ? new LinkedBlockingQueue<Runnable>(qlen) : new LinkedBlockingQueue<Runnable>(),
                new TenantThreadFactory(new ThreadGroup(threadGroup), threadIdPrefix));
    }
}
