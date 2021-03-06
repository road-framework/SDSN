package au.edu.swin.ict.road.roadtest.events;

import au.edu.swin.ict.road.roadtest.Message;

import java.util.EventObject;

/**
 * This is a Object which wraps the Message as an event.
 *
 * @author Ufuk Altin (altin.ufuk@gmail.com)
 */
public class MessageArrivedEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Message msg = null;

    /**
     * Constructor which needs the source of the object who fires the event and
     * the message it has received.
     *
     * @param source The source object is which object has fired this event.
     * @param msg    The message which has arrived as a <code>Message</code> object.
     */
    public MessageArrivedEvent(Object source, Message msg) {
        super(source);

        this.msg = msg;
    }

    /**
     * Gets the message.
     *
     * @return The <code>Message</code> object.
     */
    public Message getMessage() {
        return this.msg;
    }

}
