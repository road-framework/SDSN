package au.edu.swin.ict.road.common;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 */

public class Throughput implements Serializable, ThroughputMBean {
    private AtomicInteger used = new AtomicInteger(0);
    private int limit;

    public Throughput() {
    }

    public Throughput(int limit) {
        this.limit = limit;
    }

    public void increaseUsed() {
        used.incrementAndGet();
    }

    public void resetUsedC() {
        used.set(0);
    }

    public void decreaseUsed() {
        used.decrementAndGet();
    }

    public int getUsed() {
        return used.get();
    }

    public void setUsed(int used) {
        this.used.set(used);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public int getThroughput() {
        return limit;
    }
}
