package au.edu.swin.ict.road.roadtest.listeners;

import au.edu.swin.ict.road.roadtest.events.IntervalMessageSentEvent;

import java.util.EventListener;

/**
 * @author Ufuk Altin (altin.ufuk@gmail.com)
 */
public interface IntervalMessageSentEventListener extends EventListener {

    /**
     * @param intMsgSentEvent
     */
    void intervalMessageSent(IntervalMessageSentEvent intMsgSentEvent);
}
