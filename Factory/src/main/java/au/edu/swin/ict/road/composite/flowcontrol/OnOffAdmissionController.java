package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;

/**
 * TODO documentation
 */
public class OnOffAdmissionController implements FlowControlFunction {
    private boolean on;

    public OnOffAdmissionController(boolean on) {
        this.on = on;
    }

    @Override
    public FlowControlResult admit(IEvent event) {
        if (on) {
            return new FlowControlResult(FlowControlResult.ALLOWED, "Admitted the request");
        } else {
            return new FlowControlResult(FlowControlResult.DENIED, "Denied the request");
        }
    }

    @Override
    public String getTagName() {
        return "OnOffAdmissionController";
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }
}
