package au.edu.swin.ict.road.composite.regulation.routing;

import au.edu.swin.ict.road.composite.flowcontrol.FlowControlFunction;
import au.edu.swin.ict.road.composite.regulation.BaseRegTable;
import au.edu.swin.ict.road.composite.routing.RoutingFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RoutingRegTable extends BaseRegTable {

    private Map<String, FlowControlFunction> flowControlFunctionMap = new HashMap<String, FlowControlFunction>();
    private Map<String, RoutingFunction> routingFunctionMap = new HashMap<String, RoutingFunction>();
    private final Lock lockForFlowControl = new ReentrantLock();
    private final Lock lockForRouting = new ReentrantLock();

    public RoutingRegTable(String name) {
        super(name);
    }

    public void addFlowControlFunction(String id, FlowControlFunction flowControlFunction) {
        flowControlFunctionMap.put(id, flowControlFunction);
    }

    public void removeFlowControlFunction(String id) {
        flowControlFunctionMap.remove(id);
    }

    public FlowControlFunction getFlowControlFunction(String id) {
        return flowControlFunctionMap.get(id);
    }

    public boolean containsFlowControlFunction(String id) {
        return flowControlFunctionMap.containsKey(id);
    }

    public RoutingFunction getRoutingFunction(String id) {
        return routingFunctionMap.get(id);
    }

    public void addRoutingFunction(String id, RoutingFunction rf) {
        routingFunctionMap.put(id, rf);
    }

    public boolean containsRoutingFunction(String id) {
        return routingFunctionMap.containsKey(id);
    }

    public Lock getLockForFlowControl() {
        return lockForFlowControl;
    }

    public Lock getLockForRouting() {
        return lockForRouting;
    }
}
