package au.edu.swin.ict.serendip.core;

import au.edu.swin.ict.road.common.BaseManagedElement;
import au.edu.swin.ict.road.common.ManagementState;
import au.edu.swin.ict.road.common.ProcessManagementState;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.rules.IMonitoringRules;
import au.edu.swin.ict.road.composite.rules.drools.DroolsMonitoringRules;
import au.edu.swin.ict.road.regulator.ProcessMonitor;
import au.edu.swin.ict.road.xml.bindings.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO documentation
 */


public class ProcessDefinition extends BaseManagedElement {

    private final AtomicInteger versionNum = new AtomicInteger(1);
    private SerendipEngine serendipEngine;
    private ProcessDefinitionType type;
    private ConcurrentHashMap<String, ProcessInstance> processInstanceCollection = new ConcurrentHashMap<String, ProcessInstance>();
    private ConcurrentHashMap<String, ProcessInstance> completedInstanceCollection = new ConcurrentHashMap<String, ProcessInstance>();
    private VSNDefinition parent;
    private ProcessMonitor processMonitor;
    private PerformanceModel performanceModel;
    private Composite composite;
    private int weight = 1;
    private int threshold;
    private int minRate;
    private String version;

    public ProcessDefinition(ProcessDefinitionType type, SerendipEngine serendipEngine, Composite composite) {
        super(new ProcessManagementState(type.getId()));
        this.serendipEngine = serendipEngine;
        this.type = type;
        this.composite = composite;
        updateVersion();
        processQoSParameters();
    }

    public void updateVersion() {
        this.version = type.getId() + "." + versionNum.getAndDecrement();
    }

    public SerendipEngine getSerendipEngine() {
        return serendipEngine;
    }

    public ProcessDefinitionType getType() {
        return type;
    }

    public String getId() {
        return type.getId();
    }

    public synchronized boolean addProcessInstance(ProcessInstance instance) {
        if (getMgtState().getState().equals(ManagementState.STATE_ACTIVE)) {
            instance.setParent(this);
            processInstanceCollection.put(instance.getId(), instance);
            return true;
        }
        return false;
    }

    public ProcessInstance removeProcessInstance(String pid) {
        return processInstanceCollection.remove(pid);
    }

    public ProcessInstance getProcessInstance(String pid) {
        return processInstanceCollection.get(pid);
    }

    public ProcessInstance getCompletedProcessInstance(String pid) {
        return completedInstanceCollection.get(pid);
    }

    public void addCompletedProcessInstance(ProcessInstance instance) {
        instance.setParent(this);
        completedInstanceCollection.put(instance.getId(), instance);
    }

    public void removeAllCompletedProcessInstances() {
        completedInstanceCollection.clear();
    }

    public Collection<ProcessInstance> getAllProcessInstances() {
        return processInstanceCollection.values();
    }

    public Collection<ProcessInstance> getAllCompletedProcessInstances() {
        return completedInstanceCollection.values();
    }

    public VSNDefinition getParent() {
        return parent;
    }

    public void setParent(VSNDefinition parent) {
        this.parent = parent;
    }

    public ProcessMonitor getProcessMonitor() {
        return processMonitor;
    }

    public void setProcessMonitor(MonitorType monitorType) {

        if (monitorType != null) {

            String monitorId = monitorType.getId();
            if (monitorId == null || monitorId.isEmpty()) {
                monitorId = type.getId() + "." + "monitor";
            }
            AnalysisType analysisType = monitorType.getAnalysis();
            String monitorFileName = analysisType.getScript();

            IMonitoringRules iMonitoringRules = new DroolsMonitoringRules(parent.getId().toLowerCase() + "/" +
                    monitorFileName, composite.getRulesDir(), composite.getFTS());
            processMonitor = new ProcessMonitor(monitorId, iMonitoringRules);
            String cos = type.getCoS().trim();
            String cot = type.getCoT().trim();
            for (MonitorEventType eventType : monitorType.getEvent()) {
                String ep = eventType.getPattern().trim();
                if (!cos.equals(ep) && !cot.equals(ep)) {
                    processMonitor.addEventPattern(ep);
                }
            }
            processMonitor.addEventPattern(cos);
            processMonitor.addEventPattern(cot);
        }
    }

    public PerformanceModel getPerformanceModel() {
        return performanceModel;
    }

    public void setPerformanceModel(PerformanceModel performanceModel) {
        this.performanceModel = performanceModel;
    }

    public void removeProcessMonitor() {
        type.setMonitor(null);
        processMonitor = null;
    }

    public void setMonitorRules(String monitorFileName) {
        type.getMonitor().getAnalysis().setScript(monitorFileName);
        processMonitor.setMonitoringRules(new DroolsMonitoringRules(monitorFileName.toLowerCase(), composite.getRulesDir(), composite.getFTS()));
    }

    private void processQoSParameters() {
        TrafficModelType trafficModelType = type.getTraffic();
        if (trafficModelType != null) {
            String weightStr = trafficModelType.getPreference();
            if (weightStr != null && !weightStr.isEmpty()) {
                weight = Integer.parseInt(weightStr.trim());
            }
            String minRateStr = trafficModelType.getMinCapacity();
            if (minRateStr != null && !minRateStr.isEmpty()) {
                minRate = Integer.parseInt(minRateStr.trim());
            }
            String maxRateStr = trafficModelType.getMaxCapacity();
            if (maxRateStr != null && !maxRateStr.isEmpty()) {
                threshold = Integer.parseInt(maxRateStr.trim());
            }
        }
    }

    public int getWeight() {
        return weight;
    }

    public int getThreshold() {
        return threshold;
    }

    public int getMinRate() {
        return minRate;
    }

    public synchronized List<String> getAllLiveInstanceIds() {
        List<String> instanceMap = new ArrayList<String>();
        for (ProcessInstance pi : getAllProcessInstances()) {
            if (!pi.getMgtState().getState().equals(ManagementState.STATE_QUIESCENCE)) {
                instanceMap.add(pi.getId());
            }
        }
        return instanceMap;
    }

    public synchronized Map<String, ProcessInstance> getAllLiveInstances() {
        Map<String, ProcessInstance> instanceMap = new HashMap<String, ProcessInstance>();
        for (ProcessInstance pi : getAllProcessInstances()) {
            if (!pi.getMgtState().getState().equals(ManagementState.STATE_QUIESCENCE)) {
                instanceMap.put(pi.getId(), pi);
            }
        }
        return instanceMap;
    }

    public String getVersion() {
        return version;
    }
}
