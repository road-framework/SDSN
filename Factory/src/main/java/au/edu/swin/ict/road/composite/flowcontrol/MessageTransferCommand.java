package au.edu.swin.ict.road.composite.flowcontrol;

import au.edu.swin.ict.road.composite.MessageForwarder;
import au.edu.swin.ict.road.composite.Role;
import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.rules.events.composite.MessageReceivedAtOutPortEvent;
import org.apache.log4j.Logger;

/**
 * TODO documentation
 */
public class MessageTransferCommand implements Runnable {

    private static Logger log = Logger.getLogger(MessageTransferCommand.class.getName());
    private Role role;
    private MessageWrapper msg;

    public MessageTransferCommand(MessageReceivedAtOutPortEvent mse) {
        this.role = mse.getRole();
        this.msg = mse.getMessageWrapper();
    }

    public MessageTransferCommand(Role role, MessageWrapper msg) {
        this.role = role;
        this.msg = msg;
    }

    public void run() {
        try {
            MessageForwarder.forward(role, msg);
        } catch (Exception ignored) {
            log.error("Error occurred when forwarding message : " + ignored.getMessage(), ignored);
        }
    }
}
