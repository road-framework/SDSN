package au.edu.swin.ict.road.composite;

import au.edu.swin.ict.road.composite.message.MessageWrapper;
import au.edu.swin.ict.road.composite.message.containers.QueueListener;
import au.edu.swin.ict.road.composite.rules.ICompositeRules;
import org.apache.log4j.Logger;

/**
 * MessageDeliverer is an implementation of the worker thread pattern.
 * MessageDeliverers are assigned to functional roles and move messages from
 * source roles to destination roles via a contract.
 *
 * @author The ROAD team, Swinburne University of Technology
 */
public class MessageDeliverer implements QueueListener {

    // get the logger
    private static Logger log = Logger.getLogger(MessageDeliverer.class
                                                         .getName());

    private Role role;
    private ICompositeRules compositeRules;
//    private final ExecutorService executor;

    public MessageDeliverer(Role role, ICompositeRules compositeRules) {
        this.role = role;
        this.compositeRules = compositeRules;
//        executor = role.getComposite().getMessageDelivererWorkers();
    }

    @Override
    public void messageReceived(MessageWrapper message) {
        MessageForwarder.forward(role, message);
    }
}
