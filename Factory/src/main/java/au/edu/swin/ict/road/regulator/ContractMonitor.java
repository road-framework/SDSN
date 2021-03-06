package au.edu.swin.ict.road.regulator;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.common.RulesException;
import au.edu.swin.ict.road.composite.rules.IMonitoringRules;

/**
 * TODO documentation
 */
public class ContractMonitor {

    private String id;
    private IMonitoringRules iMonitoringRules;

    public ContractMonitor(String id, IMonitoringRules iMonitoringRules) {
        this.id = id;
        this.iMonitoringRules = iMonitoringRules;
    }

    public IMonitoringRules getMonitoringRules() {
        return iMonitoringRules;

    }

    public void setMonitoringRules(IMonitoringRules iMonitoringRules) {
        this.iMonitoringRules = iMonitoringRules;
    }

    public String getId() {
        return id;
    }

    public void addEvent(IEvent iEvent) {
        try {
            iMonitoringRules.insertEvent(iEvent);
        } catch (RulesException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
