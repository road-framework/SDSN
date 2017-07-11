package au.edu.swin.ict.road.common;


import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO documentation
 */
public class VSNState {
    private final Lock lock = new ReentrantLock();
    private String vsnId;
    private ConcurrentHashMap<String, StateRecord> cache = new ConcurrentHashMap<String, StateRecord>();
    private ConcurrentHashMap<String, ProcessState> routeStates = new ConcurrentHashMap<String, ProcessState>();
    private MBeanRegistrar registrar = MBeanRegistrar.getInstance();

    public VSNState(String vsnId) {
        this.vsnId = vsnId;
    }

    public String getVSNId() {
        return vsnId;
    }

    public ProcessState getProcessState(String processId) {
        return routeStates.get(processId);
    }

    public void addProcessState(ProcessState processState) {
        routeStates.put(processState.getProcessId(), processState);
    }

    public Collection<ProcessState> getProcessStates() {
        return routeStates.values();
    }

    public Lock getLock() {
        return lock;
    }

    public void putInCache(String key, StateRecord stateRecord) {
        Object so = stateRecord.getStateInstance();
        if (so instanceof Throughput) {
            registrar.registerMBean(so, vsnId, vsnId + "State", key);
        } else if (so instanceof ResponseTime) {
            registrar.registerMBean(so, vsnId, vsnId + "State", key);
        }
        cache.put(key, stateRecord);
    }

    public StateRecord retrieveFromCache(String key) {
        return cache.get(key);
    }

    public StateRecord removeFromCache(String key) {
        StateRecord record = cache.remove(key);
        Object so = record.getStateInstance();
        if (so instanceof Throughput) {
            registrar.unRegisterMBean(vsnId, vsnId + "State", key);
        } else if (so instanceof ResponseTime) {
            registrar.unRegisterMBean(vsnId, vsnId + "State", key);
        }
        return record;
    }
}
