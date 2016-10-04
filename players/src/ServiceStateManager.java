import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO documentation
 */
public class ServiceStateManager {

    private static ServiceStateManager ourInstance = new ServiceStateManager();

    public static ServiceStateManager getInstance() {
        return ourInstance;
    }

    private ServiceStateManager() {
    }

    private ConcurrentHashMap<String, ServiceState> serviceStateMap = new ConcurrentHashMap<String, ServiceState>();

    public void addServiceState(String serviceName, ServiceState serviceState) {
        serviceStateMap.put(serviceName, serviceState);
    }

    public ServiceState getServiceState(String serviceName) {
        return serviceStateMap.get(serviceName);
    }

    public boolean containsServiceState(String serviceName) {
        return serviceStateMap.containsKey(serviceName);
    }
}
