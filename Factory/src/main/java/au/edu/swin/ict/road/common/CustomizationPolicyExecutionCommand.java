package au.edu.swin.ict.road.common;

public class CustomizationPolicyExecutionCommand implements Runnable {

    private CustomizationPolicyExecutor managementRules;
    private IEvent iEvent;

    public CustomizationPolicyExecutionCommand(CustomizationPolicyExecutor managementRules, IEvent iEvent) {
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
