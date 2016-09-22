package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.*;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import org.apache.log4j.Logger;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * TODO documentation
 */
public class StaticFairRateController implements FlowControlFunction {

    private static Logger log = Logger.getLogger(StaticFairRateController.class);
    private int threshold = FlowControlConstraints.DEFAULT_THRESHOLD;
    private long interval = FlowControlConstraints.DEFAULT_INTERVAL;
    private long endTime = -1;
    private final Lock lock = new ReentrantLock();
    private Composite composite;

    public StaticFairRateController(long interval, int threshold, Composite composite) {
        this.interval = interval * 1000000000;      // Convert to nanosecond
        this.threshold = threshold;
        this.composite = composite;
    }

    public StaticFairRateController(String conf, Composite composite) {
        String[] pars = conf.split(",");
        setThreshold(Integer.parseInt(pars[0]));
        setInterval(Long.parseLong(pars[1]));
        this.composite = composite;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public long getInterval() {
        return (interval / 1000000000);
    }

    public void setInterval(long interval) {
        this.interval = interval * 1000000000;
    }

    @Override
    public FlowControlResult admit(IEvent event) {
        RoleServiceMessage sourceEvent = (RoleServiceMessage) event;
        sourceEvent.setBlocked(false);
        VSNState vsnState =
                composite.getServiceNetworkState().getVsnState(
                        sourceEvent.getMessageWrapper().getClassifier().getVsnId());
        lock.lock();
        vsnState.getLock().lock();
        try {
            Capacity capacity =
                    (Capacity) vsnState.retrieveFromCache(vsnState.getVSNId() + "_cap").getStateInstance();
            int limit = capacity.getUsedCapacity();
            long currentTime = System.nanoTime();
            if (endTime == -1 || (currentTime > endTime)) {
                this.endTime = currentTime + this.interval;
                // Reset at interval - no moving windows
                capacity.resetUsedCapacity();
                for (ProcessState processState : vsnState.getProcessStates()) {
                    Weight weight = (Weight) processState.retrieveFromCache(processState.getProcessId() + "_wgt").getStateInstance();
                    Capacity pCapacity =
                            (Capacity) processState.retrieveFromCache(processState.getProcessId() + "_cap").getStateInstance();
                    weight.resetCurrentAllowed();
                    pCapacity.resetUsedCapacity();
                }
            }
            if (limit >= threshold) {
                sourceEvent.setBlocked(true);
                return new FlowControlResult(FlowControlResult.DENIED,
                                             "Throughput threshold has reached : " + threshold);
            } else {
                capacity.increaseUsedCapacity();
            }
        } finally {
            vsnState.getLock().unlock();
            lock.unlock();
        }
        return new FlowControlResult(FlowControlResult.ALLOWED);
    }

//    public FlowControlResult admit(IEvent event) {
//        RoleServiceMessage msg = (RoleServiceMessage) event;
//        msg.setBlocked(false);
//        if (System.nanoTime() > endTime) {
//            msg.setBlocked(true);
//            return new FlowControlResult(FlowControlResult.SENDTOCONTROLLER, "The interval has over.");
//        }
//        if (count.incrementAndGet() > threshold) {
//            return new FlowControlResult(FlowControlResult.DENIED,
//                    "Throughput threshold has reached : " + threshold);
//        }
//        // Select the path - TODO - first only consider the slice level
//        // Get the capacity requirement for the process instance
//        // Reserve them, if succeed -> continue, otherwise drop
//        Classifier classifier = msg.getMessageWrapper().getClassifier();
//        String sliceId = classifier.getVsnId();
//        String alternative = classifier.getProcessId();
//        VSNDefinition group = composite.getSerendipEngine().getProcessDefinitionGroup(sliceId);
//        ProcessDefinition processDefinition = group.getProcessDefinition(alternative);
//        PerformanceModel performanceModel = processDefinition.getPerformanceModel();
//        for (TaskNode taskNode : performanceModel.getTaskNodes()) {
//            if (composite.getRoleByID(taskNode.getRoleId()).getRoleType().isEntryRole()) {
//                continue;
//            }
//            int capacityUnit = taskNode.getCapacityUnit();
//            String factId = sliceId + "." + taskNode.getTaskType().getId();
//            FactObject fact = composite.getFTS().getFact("Capacity", factId);
//            int reserved = 0;
//            if (fact == null) {
//                fact = new FactObject("Capacity", factId, factId);
//            } else {
//                reserved = Integer.parseInt(fact.getAttribute("reserved").toString());
//            }
//            reserved += capacityUnit;
//            if (reserved > taskNode.getThreshold()) {
//                return new FlowControlResult(FlowControlResult.DENIED, "Cannot reserve - over threshold");
//            } else {
//                fact.setAttribute("reserved", reserved);
//            }
//            for (FlowPolicyInfo info : group.getFlowPolicyInfo(taskNode.getTaskRefType().getId())) {
//                if (info.getRoleId().equals(msg.getRoleId())) {
//                    continue;
//                }
//                FlowControlPolicy flowControlPolicy =
//                        composite.getRoleByID(info.getRoleId()).getIngressFlowControlTable().getFlowControlPolicy(
//                                info.getPolicyKey());
//                if (flowControlPolicy != null) {
//                    LocalRateController rateController =
//                            (LocalRateController) flowControlPolicy.getFlowControlFunction("LocalRateController");
//                    rateController.increaseReserved(capacityUnit);
//                }
//            }
//        }
//        return new FlowControlResult(FlowControlResult.ALLOWED);
//    }

    @Override
    public String getTagName() {
        return "StaticFairRateController";
    }
}