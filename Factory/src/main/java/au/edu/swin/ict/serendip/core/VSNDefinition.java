package au.edu.swin.ict.serendip.core;

import au.edu.swin.ict.road.common.VSN;
import au.edu.swin.ict.road.composite.flowcontrol.FlowControlConstraints;
import au.edu.swin.ict.road.xml.bindings.ProcessDefinitionsType;

import java.util.*;

/**
 * TODO documentation
 */
public class VSNDefinition {

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    private Map<String, ProcessDefinition> processes = new HashMap<String, ProcessDefinition>();

    private ProcessDefinitionsType type;
    private Map<String, List<FlowPolicyInfo>> flowPolicyInfos = new HashMap<String, List<FlowPolicyInfo>>();

    private long interval = FlowControlConstraints.DEFAULT_INTERVAL;
    private int threshold = FlowControlConstraints.DEFAULT_THRESHOLD;
    private VSN mgtState;

    public VSNDefinition(ProcessDefinitionsType type, SerendipEngine serendipEngine) {
        this.type = type;
        this.serendipEngine = serendipEngine;
        this.mgtState = new VSN(type.getId());
    }

    public ProcessDefinitionsType getType() {
        return type;
    }

    public SerendipEngine getSerendipEngine() {
        return serendipEngine;
    }

    private SerendipEngine serendipEngine;

    public void addProcessDefinition(ProcessDefinition processDefinition) {
        processDefinition.setParent(this);
        processes.put(processDefinition.getId(), processDefinition);
    }

    public ProcessDefinition getProcessDefinition(String id) {
        return processes.get(id);
    }

    public ProcessDefinition removeProcessDefinition(String id) {
        return processes.remove(id);
    }

    public String getId() {
        return type.getId();
    }

    public Collection<ProcessDefinition> getAllProcessDefinitions() {
        return processes.values();
    }

    public void addLocalFlowPolicyKey(String taskRef, FlowPolicyInfo key) {
        List<FlowPolicyInfo> keys = flowPolicyInfos.get(taskRef);
        if (keys == null) {
            keys = new ArrayList<FlowPolicyInfo>();
            flowPolicyInfos.put(taskRef, keys);
        }
        keys.add(key);
    }

    public Collection<FlowPolicyInfo> getFlowPolicyInfo(String taskRef) {
        return flowPolicyInfos.get(taskRef);
    }

    public boolean contains(String taskRef, String policyKey) {
        List<FlowPolicyInfo> keys = flowPolicyInfos.get(taskRef);
        if (keys == null) {
            return false;
        }
        for (FlowPolicyInfo info : keys) {
            if (info.getPolicyKey().equals(policyKey)) {
                return true;
            }
        }
        return false;
    }

    public int getNoOfProcess() {
        return processes.size();
    }

    public Collection<String> getProcessIds() {
        return processes.keySet();
    }

    public VSN getMgtState() {
        return mgtState;
    }
}
