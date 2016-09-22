package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;

/**
 * TODO documentation
 */
public interface FlowControlFunction {

    public FlowControlResult admit(IEvent msg);

    public String getTagName();
}
