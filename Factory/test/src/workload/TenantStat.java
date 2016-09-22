package workload;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO documentation
 */
public class TenantStat {
    private final Lock lock = new ReentrantLock();
    private AtomicInteger completed = new AtomicInteger(0);
    private AtomicInteger failures = new AtomicInteger(0);
    private String tenantName;
    private int totalUsers;
    private SampleStat sampleStat;

    public TenantStat(String tenantName, int totalUsers, SampleStat sampleStat) {
        this.totalUsers = totalUsers;
        this.sampleStat = sampleStat;
        this.tenantName = tenantName;
    }

    public int getCompleted() {
        return completed.get();
    }

    public void increaseCompleted() {
        lock.lock();
        try {
            this.completed.incrementAndGet();
            if (isComplete()) {
                sampleStat.notifyTenantCompletion();
            }
        } finally {
            lock.unlock();
        }
    }

    public int getFailures() {
        return failures.get();
    }

    public void increaseFailures() {
        lock.lock();
        try {
            this.failures.incrementAndGet();
            if (isComplete()) {
                sampleStat.notifyTenantCompletion();
            }
        } finally {
            lock.unlock();
        }
    }

    public String getTenantName() {
        return tenantName;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public boolean isComplete() {
        return (totalUsers == getCompleted() + getFailures());
    }
}
