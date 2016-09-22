package au.edu.swin.ict.road.composite.utills;

/**
 * TODO documentation
 */

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is the thread factory for Synapse threads which are accessible through the
 * SynapseEnvironment as pooled threads.
 */
public class ROADThreadFactory implements ThreadFactory {

    /**
     * Holds the ThreadGroup under which this factory creates threads
     */
    private final ThreadGroup group;

    /**
     * Holds the AtomicInteger class instance for the factory
     */
    private final AtomicInteger count;

    /**
     * prefix for the thread id, thread number will be followed to construct the id
     */
    private final String namePrefix;

    /**
     * Constructor for the ThreadFactory to create new threads
     *
     * @param group      - all the threads are created under this group by this factory
     * @param namePrefix - name prefix of the threads created by this factory
     */
    public ROADThreadFactory(final ThreadGroup group, final String namePrefix) {
        super();
        this.count = new AtomicInteger(1);
        this.group = group;
        this.namePrefix = namePrefix;
    }

    /**
     * This method is the implementation of the the newThread method and will
     * create new threads under the group and with the nameprefix followed by the
     * thread number as the id
     *
     * @param runnable - Runnable class to forward by the created thread
     * @return a Thread executing the given runnable
     */
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
