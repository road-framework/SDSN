package au.edu.swin.ict.road.regulator;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.SerendipEventListener;

import java.util.List;

/**
 * TODO documentation
 */
public class BehaviorMonitorInstance extends SerendipEventListener {

    private BehaviorMonitor behaviorMonitor;

    public BehaviorMonitorInstance(BehaviorMonitor behaviorMonitor) {
        this.behaviorMonitor = behaviorMonitor;
    }

    @Override
    public void eventPatternMatched(String ep, Classifier classifier) throws SerendipException {
        behaviorMonitor.eventPatternMatched(ep, classifier);
    }

    @Override
    public String getId() {
        return behaviorMonitor.getId();
    }

    public List<String> getEventPatterns() {
        return behaviorMonitor.getEventPatterns();
    }

    public void setEventPatterns(List<String> eventPatterns) {
        this.behaviorMonitor.setEventPatterns(eventPatterns);
    }

    public void addEventPattern(String eventPattern) {
        this.behaviorMonitor.addEventPattern(eventPattern.trim());
    }
}