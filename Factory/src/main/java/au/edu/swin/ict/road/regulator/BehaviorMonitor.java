package au.edu.swin.ict.road.regulator;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.rules.IMonitoringRules;
import au.edu.swin.ict.road.composite.rules.events.composite.PatternMatchedEvent;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.SerendipEventListener;

/**
 * TODO documentation
 */
public class BehaviorMonitor extends SerendipEventListener {

    public BehaviorMonitor(String id, IMonitoringRules iMonitoringRules) {
        this.id = id;
        this.iMonitoringRules = iMonitoringRules;
    }

    public IMonitoringRules getMonitoringRules() {
        return iMonitoringRules;
    }

    private String id;

    public void setMonitoringRules(IMonitoringRules iMonitoringRules) {
        this.iMonitoringRules = iMonitoringRules;
    }

    private IMonitoringRules iMonitoringRules;

    public void eventPatternMatched(String ep, Classifier classifier) throws SerendipException {
        PatternMatchedEvent event = new PatternMatchedEvent(ep, classifier);
        try {
            iMonitoringRules.insertEvent(event);
        } catch (RulesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getId() {
        return id;
    }
}
