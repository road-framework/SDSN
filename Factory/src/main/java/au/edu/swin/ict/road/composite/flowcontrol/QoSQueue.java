package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.events.composite.MessageReceivedAtOutPortEvent;
import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO documentation
 */


public class QoSQueue {

    private static Logger log = Logger.getLogger(SimpleRRScheduler.class.getName());
    private long interval = FlowControlConstraints.DEFAULT_INTERVAL;
    private long endTime;
    private AtomicInteger count = new AtomicInteger(0);
    private String roleId;
    private String id;
    private int threshold = FlowControlConstraints.DEFAULT_THRESHOLD;
    private int minRate = FlowControlConstraints.DEFAULT_MINRATE;
    private int fairRate = FlowControlConstraints.DEFAULT_MINRATE;
    private LinkedBlockingQueue<MessageReceivedAtOutPortEvent> queue =
            new LinkedBlockingQueue<MessageReceivedAtOutPortEvent>();

    public QoSQueue(String roleId, String id) {
        this.roleId = roleId;
        this.id = id;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.endTime = System.nanoTime() + interval;
        this.interval = interval;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getMinRate() {
        return minRate;
    }

    public void setMinRate(int minRate) {
        this.minRate = minRate;
    }

    public int getFairRate() {
        return fairRate;
    }

    public void setFairRate(int fairRate) {
        this.fairRate = fairRate;
    }

    public boolean canRead() {
        return count.get() < minRate || reset();
    }

    public int getCurrentQueueLength() {
        return queue.size();
    }

    public FlowControlResult enqueue(MessageReceivedAtOutPortEvent msg) {
        // Here we assume the threshold check has been already done by the admission controller
        reset();
        try {
            queue.offer(msg, 60 * 1000, TimeUnit.MILLISECONDS);
            if (log.isInfoEnabled()) {
                log.info("Queue the message : " +
                        msg.getMessageWrapper().getMessageId() + " at " + roleId + " in the queue : " + id);
            }
        } catch (InterruptedException ignored) {
            log.error("Error queuing the message : " + msg.getMessageWrapper().getMessageId()
                    + " , " + ignored.getMessage(), ignored);
        }
        return new FlowControlResult(FlowControlResult.ALLOWED, "Enqueued the message successfully");
    }

    public MessageReceivedAtOutPortEvent dequeue() {
        // here assume canread already has been called
        MessageReceivedAtOutPortEvent mse = queue.poll();
        if (mse != null) {
            count.incrementAndGet();
        }
        return mse;
    }

    private boolean reset() {
        long currentTime = System.nanoTime();
        if (currentTime > endTime) {
            synchronized (this) {
                if (currentTime > endTime) {
                    count.set(0);
                    endTime = interval + currentTime;
                    return true;
                }
            }
        }
        return false;
    }

    public int getWaitingSize() {
        return queue.size();
    }
}
