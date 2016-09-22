package au.edu.swin.ict.road.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO
 */
public class ProcessInstanceState {
    private ConcurrentHashMap<String, StateRecord> cache = new ConcurrentHashMap<String, StateRecord>();
    private final Lock lock = new ReentrantLock();
    private String processInsId;

    public ProcessInstanceState(String processInsId) {
        this.processInsId = processInsId;
    }

    public String getProcessInsId() {
        return processInsId;
    }

    public Lock getLock() {
        return lock;
    }

    public void putInCache(String key, StateRecord stateRecord) {
        cache.put(key, stateRecord);
    }

    public StateRecord removeFromCache(String key) {
        return cache.remove(key);
    }

    public StateRecord retrieveFromCache(String key) {
        return cache.get(key);
    }
}
