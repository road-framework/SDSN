package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.road.regulator.FactTupleSpace;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO documentation
 */
public class LocalRateController implements FlowControlFunction {


    private int threshold = FlowControlConstraints.DEFAULT_THRESHOLD;
    private long interval = FlowControlConstraints.DEFAULT_INTERVAL;
    private int reserved = 0;
    private long endTime;
    private AtomicInteger count;
    private FactTupleSpace factTupleSpace;

    public LocalRateController(long interval, int threshold, FactTupleSpace factTupleSpace) {
        this.interval = interval * 1000000000;      // Convert to nanosecond
        this.threshold = threshold;
        this.factTupleSpace = factTupleSpace;
        this.endTime = System.nanoTime() + this.interval;
        this.count = new AtomicInteger(0);
        if (threshold < 0) {
            this.reserved = threshold;
        }
    }

    public int getReserved() {
        return reserved;
    }

    public void setReserved(int reserved) {
        this.reserved = reserved;
    }

    public void increaseReserved(int increment) {
        this.reserved += increment;
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
        RoleServiceMessage msg = (RoleServiceMessage) event;
        msg.setBlocked(false);
        if (System.nanoTime() > endTime) {
            msg.setBlocked(true);
            return new FlowControlResult(FlowControlResult.SENDTOCONTROLLER, "The interval has over.");
        }
        if (reserved < 0) {   // Infinite resource
            return new FlowControlResult(FlowControlResult.ALLOWED);
        }
        if (count.incrementAndGet() > reserved) {
            return new FlowControlResult(FlowControlResult.DENIED,
                    "Throughput threshold has reached : " + reserved);
        }
        return new FlowControlResult(FlowControlResult.ALLOWED);
    }

    @Override
    public String getTagName() {
        return "LocalRateController";
    }
}
