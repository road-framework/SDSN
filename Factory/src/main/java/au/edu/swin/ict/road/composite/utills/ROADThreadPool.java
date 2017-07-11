package au.edu.swin.ict.road.composite.utills;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO documentation
 */

public class ROADThreadPool extends ThreadPoolExecutor {

    // default values
    public static final int ROAD_CORE_THREADS = 20;
    public static final int ROAD_MAX_THREADS = 100;
    public static final int ROAD_KEEP_ALIVE = 5;
    public static final int ROAD_THREAD_QLEN = -1;
    public static final String ROAD_THREAD_GROUP = "road-thread-group";
    public static final String ROAD_THREAD_ID_PREFIX = "Worker";

    // property keys
    public static final String THREAD_CORE = "threads.core";
    public static final String THREAD_MAX = "threads.max";
    public static final String THREAD_ALIVE = "threads.keepalive";
    public static final String THREAD_QLEN = "threads.qlen";
    public static final String THREAD_GROUP = "threads.group";
    public static final String THREAD_IDPREFIX = "threads.idprefix";

    /**
     * Constructor for the ROAD thread poll
     *
     * @param corePoolSize    - number of threads to keep in the pool, even if they are idle
     * @param maximumPoolSize - the maximum number of threads to allow in the pool
     * @param keepAliveTime   - this is the maximum time that excess idle threads will wait
     *                        for new tasks before terminating.
     * @param unit            - the time unit for the keepAliveTime argument.
     * @param workQueue       - the queue to use for holding tasks before they are executed.
     */
    public ROADThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                          TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
                new ROADThreadFactory(
                        new ThreadGroup(ROAD_THREAD_GROUP), ROAD_THREAD_ID_PREFIX));
    }

    /**
     * Default Constructor for the thread pool and will use all the values as default
     */
    public ROADThreadPool() {
        this(ROAD_CORE_THREADS, ROAD_MAX_THREADS, ROAD_KEEP_ALIVE,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    /**
     * Constructor for the ROADThreadPool
     *
     * @param corePoolSize   - number of threads to keep in the pool, even if they are idle
     * @param maxPoolSize    - the maximum number of threads to allow in the pool
     * @param keepAliveTime  - this is the maximum time that excess idle threads will wait
     *                       for new tasks before terminating.
     * @param qlen           - Thread Blocking Queue length
     * @param threadGroup    - ThreadGroup name
     * @param threadIdPrefix - Thread id prefix
     */
    public ROADThreadPool(int corePoolSize, int maxPoolSize, long keepAliveTime, int qlen,
                          String threadGroup, String threadIdPrefix) {
        super(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
                qlen > 0 ? new LinkedBlockingQueue<Runnable>(qlen) : new LinkedBlockingQueue<Runnable>(),
                new ROADThreadFactory(new ThreadGroup(threadGroup), threadIdPrefix));
    }

}
