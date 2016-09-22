package au.edu.swin.ict.road.common;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO
 */
public class Capacity implements Serializable {
    private static final long serialVersionUID = -1688013433935599352L;
    private String id;
    private int limit;
    private AtomicInteger used = new AtomicInteger(0);

    public Capacity() {
    }

    public int getUsedCapacity() {
        return used.get();
    }

    public void increaseUsedCapacity() {
        used.incrementAndGet();
    }

    public void resetUsedCapacity() {
        used.set(0);
    }

    public void decreaseUsedCapacity() {
        used.decrementAndGet();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
