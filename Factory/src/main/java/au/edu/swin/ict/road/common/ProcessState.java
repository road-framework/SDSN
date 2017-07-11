package au.edu.swin.ict.road.common;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO documentation
 */
public class ProcessState {
    private final Lock lock = new ReentrantLock();
    private ConcurrentHashMap<String, StateRecord> cache = new ConcurrentHashMap<String, StateRecord>();
    private String processId;
    private String vsnId;
    private ConcurrentHashMap<String, ProcessInstanceState> processInstanceStates = new ConcurrentHashMap<String, ProcessInstanceState>();
    private MBeanRegistrar registrar = MBeanRegistrar.getInstance();

    public ProcessState(String vsnId, String processId) {
        this.processId = processId;
        this.vsnId = vsnId;
    }

    public String getProcessId() {
        return processId;
    }

    public Lock getLock() {
        return lock;
    }

    public void putInCache(String key, StateRecord stateRecord) {
        Object so = stateRecord.getStateInstance();
        if (so instanceof Throughput) {
            registrar.registerMBean(so, vsnId, processId + "State", key);
        } else if (so instanceof ResponseTime) {
            registrar.registerMBean(so, vsnId, processId + "State", key);
        }
        cache.put(key, stateRecord);
    }

    public StateRecord retrieveFromCache(String key) {
        return cache.get(key);
    }

    public ProcessInstanceState getProcessInstanceState(String processInsId) {
        return processInstanceStates.get(processInsId);
    }

    public void addProcessInstanceState(ProcessInstanceState processInstanceState) {
        processInstanceStates.put(processInstanceState.getProcessInsId(), processInstanceState);
    }

    public Collection<ProcessInstanceState> getProcessInstanceStates() {
        return processInstanceStates.values();
    }

    public StateRecord removeFromCache(String key) {
        StateRecord record = cache.remove(key);
        Object so = record.getStateInstance();
        if (so instanceof Throughput) {
            registrar.unRegisterMBean(vsnId, processId + "State", key);
        } else if (so instanceof ResponseTime) {
            registrar.unRegisterMBean(vsnId, processId + "State", key);
        }
        return record;
    }
}
