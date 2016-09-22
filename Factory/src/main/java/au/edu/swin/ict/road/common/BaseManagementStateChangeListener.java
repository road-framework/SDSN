package au.edu.swin.ict.road.common;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO
 */
public class BaseManagementStateChangeListener implements ManagementStateChangeListener {

    private final ConcurrentHashMap<String, IEvent> managementStates =
            new ConcurrentHashMap<String, IEvent>();

    @Override
    public void notify(ManagementState managementState) {
        managementStates.put(managementState.getId(), managementState);
    }

    @Override
    public void notifyRemoval(ManagementState managementState) {
        managementStates.remove(managementState.getId());
    }

    protected Collection<IEvent> getManagementStates() {
        return managementStates.values();
    }
}
