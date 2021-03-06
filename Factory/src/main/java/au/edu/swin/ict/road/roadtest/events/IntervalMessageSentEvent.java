package au.edu.swin.ict.road.roadtest.events;

import au.edu.swin.ict.road.roadtest.IntervalMessage;

import java.util.EventObject;

/**
 * @author Ufuk Altin (altin.ufuk@gmail.com)
 */
public class IntervalMessageSentEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private IntervalMessage intervalMessage;

    /**
     * @param source
     * @param intervalMessage
     */
    public IntervalMessageSentEvent(Object source, IntervalMessage intervalMessage) {
        super(source);
        this.intervalMessage = intervalMessage;
    }

    /**
     * @return IntervalMessage object
     */
    public IntervalMessage getIntervalMessage() {
        return this.intervalMessage;
    }

}
