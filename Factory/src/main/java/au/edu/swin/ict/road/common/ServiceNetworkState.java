package au.edu.swin.ict.road.common;

import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO documentation
 */
public class ServiceNetworkState {
    private String name;
    private ConcurrentHashMap<String, VSNState> tenantStateMap = new ConcurrentHashMap<String, VSNState>();
    private ConcurrentHashMap<String, StateRecord> cache = new ConcurrentHashMap<String, StateRecord>();

    public ServiceNetworkState(String name) {
        this.name = name;
    }

    public VSNState getVsnState(String vsnName) {
        return tenantStateMap.get(vsnName);
    }

    public void addVsnState(VSNState VSNState) {
        tenantStateMap.put(VSNState.getVSNId(), VSNState);
    }

    public String getName() {
        return name;
    }

    public void putInCache(String key, StateRecord stateRecord) {
        cache.put(key, stateRecord);
    }

    public StateRecord retrieveFromCache(String key) {
        return cache.get(key);
    }

    public StateRecord removeFromCache(String key) {
        return cache.remove(key);
    }
}
