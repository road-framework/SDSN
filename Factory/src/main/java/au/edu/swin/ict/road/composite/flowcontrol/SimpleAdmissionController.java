package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO documentation
 */
public class SimpleAdmissionController implements FlowControlFunction {

    private int threshold = FlowControlConstraints.DEFAULT_THRESHOLD;
    private long interval = FlowControlConstraints.DEFAULT_INTERVAL;
    private long endTime;
    private AtomicInteger count;
    private String config;


    public SimpleAdmissionController(long interval, int threshold) {
        this.interval = interval * 1000000000;      // Convert to nanosecond
        this.threshold = threshold;
        this.endTime = System.nanoTime() + this.interval;
        this.count = new AtomicInteger(0);
    }

    public SimpleAdmissionController(String conf) {
        this.config = conf.trim();
        String[] pars = conf.split(",");
        setThreshold(Integer.parseInt(pars[0]));
        setInterval(Long.parseLong(pars[1]));
        this.endTime = System.nanoTime() + this.interval;
        this.count = new AtomicInteger(0);
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
            synchronized (this) {
                this.endTime = System.nanoTime() + this.interval;
                this.count.set(0);
            }
        }
        int index;
        synchronized (this) {
            index = count.getAndIncrement();
        }
        if (index > threshold) {
            return new FlowControlResult(FlowControlResult.DENIED, "Throughput threshold has reached : " + threshold);
        }
        return new FlowControlResult(FlowControlResult.ALLOWED);
    }

    @Override
    public String getTagName() {
        return "SimpleAdmissionController";
    }

    public String getConfig() {
        return config;
    }

    public boolean isNewConfig(String conf) {
        if (config == null) {
            return true;
        }
        return !conf.trim().equals(config.trim());
    }

}