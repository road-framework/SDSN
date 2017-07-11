package au.edu.swin.ict.serendip.event;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.serendip.core.SerendipException;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Any class that need to subscribe for event patterns should extend the
 * <code>SerendipEventListener</code> Current child classes are
 * <ul>
 * <li>Task</li>
 * <li>ProcessInstance</li>
 * </ul>
 *
 * @author Malinda Kapuruge
 */
public abstract class SerendipEventListener implements EventListener {

    private final List<String> triggeredEvents = new ArrayList<String>();
    private final Lock lock = new ReentrantLock();
    private String currentMatchedPattern;
    private List<String> eventPatterns = new ArrayList<String>();
    private Classifier classifier = null;
//    private List<String> preEPIds;

//    public List<String> getPreEPIds() {
//        return preEPIds;
//    }
//
//    public void setPreEPIds(List<String> preEPIds) {
//        this.preEPIds = preEPIds;
//    }

    public String getCurrentMatchedPattern() {
        return currentMatchedPattern;
    }

    public void setCurrentMatchedPattern(String currentMatchedPattern) {
        this.currentMatchedPattern = currentMatchedPattern;
    }

    public List<String> getEventPatterns() {
        return eventPatterns;
    }

    public void setEventPatterns(List<String> eventPatterns) {
        this.eventPatterns = eventPatterns;
    }

    public void addEventPattern(String eventPattern) {
        this.eventPatterns.add(eventPattern.trim());
    }

    public abstract void eventPatternMatched(String ep, Classifier classifier) throws SerendipException;

    public abstract String getId();

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public Lock getLock() {
        return lock;
    }

    public void markTriggered(String eventPattern) {
        triggeredEvents.add(eventPattern);
    }

    public void unMarkTriggered(String eventPattern) {
        triggeredEvents.remove(eventPattern);
    }

    public boolean isTriggered(String eventPattern) {
        return triggeredEvents.contains(eventPattern);
    }

    public void removeEventPattern(String pattern) {
        eventPatterns.remove(pattern);
    }

    public void removeAllEventPatterns() {
        eventPatterns.clear();
    }
}
