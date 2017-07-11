package au.edu.swin.ict.road.composite.regulation;

import au.edu.swin.ict.road.composite.routing.RoutingFunction;
import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * TODO
 */
public class RoleServiceMessageQueue implements RoutingFunction {

    private static Logger log = Logger.getLogger(RoleServiceMessageQueue.class.getName());
    private String config;

    private LinkedBlockingQueue<RoleServiceMessage> queue = new LinkedBlockingQueue<RoleServiceMessage>();

    public RoleServiceMessageQueue(String config) {
        this.config = config;
    }

    public void enqueue(RoleServiceMessage msg) {
        try {
            queue.offer(msg, 60 * 1000, TimeUnit.MILLISECONDS);
            if (log.isInfoEnabled()) {
                log.info("Queue the message : " + msg.getMessageWrapper().getMessageId());
            }
        } catch (InterruptedException ignored) {
            log.error("Error queuing the message : " + msg.getMessageWrapper().getMessageId()
                    + " , " + ignored.getMessage(), ignored);
        }
    }

    public RoleServiceMessage dequeue() {
        return queue.poll();
    }

    public List<RoleServiceMessage> dequeueAll() {
        List<RoleServiceMessage> messages = new ArrayList<RoleServiceMessage>();
        queue.drainTo(messages);
        return messages;
    }

    @Override
    public void execute(RoleServiceMessage msg) {
        enqueue(msg);
    }

    @Override
    public String getTagName() {
        return "SimpleQueue";
    }
}
