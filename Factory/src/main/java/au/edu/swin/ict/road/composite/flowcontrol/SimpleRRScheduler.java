package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.IEvent;
import au.edu.swin.ict.road.composite.Composite;
import au.edu.swin.ict.road.composite.rules.FlowControlResult;
import au.edu.swin.ict.road.composite.rules.events.composite.MessageReceivedAtOutPortEvent;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * TODO documentation
 */
public class SimpleRRScheduler extends TimerTask implements FlowControlFunction, Runnable {

    private static Logger log = Logger.getLogger(SimpleRRScheduler.class.getName());

    private final Map<String, LinkedBlockingQueue<MessageReceivedAtOutPortEvent>> queues =
            new HashMap<String, LinkedBlockingQueue<MessageReceivedAtOutPortEvent>>();
    private String roleId;
    private String interaction;
    private Composite composite;

    public SimpleRRScheduler(String roleId, String interaction, Composite composite) {
        this.roleId = roleId;
        this.interaction = interaction;
        this.composite = composite;
        composite.getTimeOutTimer().scheduleAtFixedRate(this, 1000, 1000);
    }

    public FlowControlResult admit(IEvent event) {
        MessageReceivedAtOutPortEvent msg = (MessageReceivedAtOutPortEvent) event;
        Classifier classifier = msg.getMessageWrapper().getClassifier();
        String queueId = classifier.getVsnId() + "." + classifier.getProcessId();
        LinkedBlockingQueue<MessageReceivedAtOutPortEvent> queue = queues.get(queueId);
        try {
            queue.offer(msg, 60 * 1000, TimeUnit.MILLISECONDS);
            if (log.isInfoEnabled()) {
                log.info("Queue the message : " +
                         msg.getMessageWrapper().getMessageId() + " at " + roleId + " in the queue : " + queueId);
            }
        } catch (InterruptedException ignored) {
            log.error("Error queuing the message : " + msg.getMessageWrapper().getMessageId()
                      + " , " + ignored.getMessage(), ignored);
        }
        return new FlowControlResult(FlowControlResult.ALLOWED, "Enqueued the message successfully");
    }

    @Override
    public String getTagName() {
        return "SimpleRRScheduler";
    }

    public void addQueue(String queueId, LinkedBlockingQueue<MessageReceivedAtOutPortEvent> queue) {
        queues.put(queueId, queue);
    }

    public boolean containQueue(String queueId) {
        return queues.containsKey(queueId);
    }

    @Override
    public void run() {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Executing the scheduler at the outport : " + interaction +
                          " of the role : " + roleId);
            }
            for (Queue<MessageReceivedAtOutPortEvent> queue : queues.values()) {
                MessageReceivedAtOutPortEvent mse = queue.poll();
                if (mse != null) {
                    composite.getMessageDelivererWorkers().execute(new MessageTransferCommand(mse));
                }
            }
        } catch (Exception ignored) {
            log.error("Error occurred when scheduling : " + ignored.getMessage(), ignored);
        }
    }
}
