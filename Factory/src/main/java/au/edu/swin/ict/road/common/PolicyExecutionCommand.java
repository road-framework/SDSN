package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class PolicyExecutionCommand implements Runnable {
    private DroolsManagementRules managementRules;
    private IEvent iEvent;

    public PolicyExecutionCommand(DroolsManagementRules managementRules, IEvent iEvent) {
        this.managementRules = managementRules;
        this.iEvent = iEvent;
    }

    @Override
    public void run() {
        try {
            managementRules.insertEvent(iEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
