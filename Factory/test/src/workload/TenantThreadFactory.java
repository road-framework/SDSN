package workload;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO documentation
 */
public class TenantThreadFactory implements ThreadFactory {
    private final ThreadGroup group;
    private final AtomicInteger count;
    private final String namePrefix;


    public TenantThreadFactory(final ThreadGroup group, final String namePrefix) {
        super();
        this.count = new AtomicInteger(1);
        this.group = group;
        this.namePrefix = namePrefix;
    }

    public Thread newThread(final Runnable runnable) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(this.namePrefix);
        buffer.append('-');
        buffer.append(this.count.getAndIncrement());
        Thread t = new Thread(group, runnable, buffer.toString(), 0);
        t.setDaemon(false);
        t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
