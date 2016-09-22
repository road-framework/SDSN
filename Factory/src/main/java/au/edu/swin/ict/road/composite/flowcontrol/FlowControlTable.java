package au.edu.swin.ict.road.composite.flowcontrol;


import java.util.HashMap;
import java.util.Map;

/**
 * TODO documentation
 */
public class FlowControlTable {

    private final Map<String, FlowControlPolicy> policyMap = new HashMap<String, FlowControlPolicy>();

    public void addFlowControlPolicy(String key, FlowControlPolicy policy) {
        policyMap.put(key, policy);
    }

    public FlowControlPolicy getFlowControlPolicy(String key) {
        return policyMap.get(key);
    }

    public FlowControlPolicy removeFlowControlPolicy(String key) {
        return policyMap.remove(key);
    }
}
