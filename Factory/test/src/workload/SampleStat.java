package workload;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO documentation
 */
public class SampleStat {
    private final Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private int sampleId;
    private long timeSpent;
    private List<TenantStat> tenantStats = new ArrayList<TenantStat>();

    public SampleStat(int sampleId) {
        this.sampleId = sampleId;
    }

    public int getSampleId() {
        return sampleId;
    }

    public void setSampleId(int sampleId) {
        this.sampleId = sampleId;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public void addTenantStat(TenantStat tenantStat) {
        tenantStats.add(tenantStat);
    }

    public List<TenantStat> getTenantStats() {
        return tenantStats;
    }

    public void waitTillComplete() {
        lock.lock();
        try {
            notFull.await();
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            lock.unlock();
        }
    }

    public void notifyTenantCompletion() {
        lock.lock();
        try {
            boolean complete = true;
            for (TenantStat tenantStat : tenantStats) {
                if (!tenantStat.isComplete()) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                notFull.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
