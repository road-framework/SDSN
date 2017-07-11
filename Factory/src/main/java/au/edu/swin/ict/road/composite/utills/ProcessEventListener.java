package au.edu.swin.ict.road.composite.utills;

import au.edu.swin.ict.serendip.event.EventCloud;
import au.edu.swin.ict.serendip.event.SerendipEventListener;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.locks.Lock;

public class ProcessEventListener {

    private static Logger log = Logger.getLogger(ProcessEventListener.class);
    private Map<String, List<SerendipEventListener>> eventListeners = new HashMap<String, List<SerendipEventListener>>();
    //    private SerendipEventListener processInstance;
    private String id;

    public ProcessEventListener(String id) {
        this.id = id;
    }

    public void addSerendipEventListener(SerendipEventListener serendipEventListener) {
//        if (processInstance == null && serendipEventListener.getId().equals(id)) {
//            processInstance = serendipEventListener;
//            return;
//        }
        for (String ep : serendipEventListener.getEventPatterns()) {
            register(ep, serendipEventListener);
        }
    }

    private void register(String key, SerendipEventListener serendipEventListener) {
        if (key == null || key.isEmpty()) {
            if (log.isInfoEnabled()) {
                log.info("Event pattern is empty");
            }
        } else {
            List<SerendipEventListener> list = eventListeners.get(key);
            if (list == null) {
                list = new ArrayList<SerendipEventListener>();
                eventListeners.put(key, list);
            }
            list.add(serendipEventListener);
        }
    }

    public void removeSerendipEventListener(String id) {
        eventListeners.remove(id);
    }

    public boolean isContains(String id) {
        return eventListeners.containsKey(id);
    }

    public Collection<SerendipEventListener> getSerendipEventListener(EventCloud eventCloud, String eventID) {
        List<SerendipEventListener> listeners = new ArrayList<SerendipEventListener>();
        for (String pattern : eventListeners.keySet()) {
            if (pattern.contains(eventID)) {
                List<SerendipEventListener> list = eventListeners.get(pattern);
                for (SerendipEventListener sel : list) {
                    if (isMatched(sel, pattern, eventID, eventCloud)) {
                        listeners.add(sel);
                    }
                }
            }
        }

//        if (processInstance.getEventPattern().contains(eventID)) {
//            listeners.add(processInstance); //To make sure the process instance is the last one //TODO
//        }
        return listeners;
    }

    private boolean isMatched(SerendipEventListener sel, String eventPattern, String eventID, EventCloud eventCloud) {
        if (!sel.isTriggered(eventPattern)) {
            Lock lock = sel.getLock();
            try {
                lock.lock();
                if (eventCloud.isPatternMatched(eventPattern, sel.getClassifier().getProcessInsId())) {
                    sel.markTriggered(eventPattern);
                    sel.setCurrentMatchedPattern(eventPattern);
                    return true;
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Pattern did not match " + eventPattern + " for process InsId : " +
                                sel.getClassifier().getProcessInsId());
                    }
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }
}
