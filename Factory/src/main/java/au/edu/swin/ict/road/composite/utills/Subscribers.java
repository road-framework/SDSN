package au.edu.swin.ict.road.composite.utills;

import au.edu.swin.ict.serendip.event.SerendipEventListener;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO documentation
 */
public class Subscribers {
    private static Logger log = Logger.getLogger(Subscribers.class);
    private String id;
    private Map<String, List<SerendipEventListener>> subscribers = new HashMap<String, List<SerendipEventListener>>();

    public Subscribers(String id) {
        this.id = id;
    }

    public void addSubscriber(String eId, SerendipEventListener listener) {
        List<SerendipEventListener> listeners = subscribers.get(eId);
        if (listeners == null) {
            listeners = new ArrayList<SerendipEventListener>();
            subscribers.put(eId, listeners);
        }
        listeners.add(listener);
    }

    public boolean isInterested(String eID) {
        return subscribers.containsKey(eID);
    }

    public List<SerendipEventListener> getSubscribers(String eId) {
        return subscribers.get(eId);
    }
}
