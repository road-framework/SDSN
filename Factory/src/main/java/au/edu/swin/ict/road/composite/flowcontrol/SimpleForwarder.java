package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.events.composite.MessageReceivedAtOutPortEvent;

/**
 * TODO documentation
 */
public class SimpleForwarder implements FlowControlFunction {

    private Composite composite;

    public SimpleForwarder(Composite composite) {
        this.composite = composite;
    }

    @Override
    public FlowControlResult admit(IEvent event) {
        composite.getMessageDelivererWorkers().execute(
                new MessageTransferCommand((MessageReceivedAtOutPortEvent) event));
        return new FlowControlResult(FlowControlResult.ALLOWED, "Forwarded the message successfully");
    }

    @Override
    public String getTagName() {
        return "SimpleForwarder";
    }
}
