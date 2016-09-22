package au.edu.swin.ict.road.roadtest.listeners;

import au.edu.swin.ict.road.roadtest.events.MessageSentEvent;

import java.util.EventListener;

/**
 * @author Ufuk Altin (altin.ufuk@gmail.com)
 */
public interface MessageSentEventListener extends EventListener {
    /**
     * @param msgSentEvent
     */
    void messageSent(MessageSentEvent msgSentEvent);
}
