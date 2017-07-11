package au.edu.swin.ict.road.composite.rules.events.composite;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.IEvent;

/**
 * TODO documentation
 */
public class PatternMatchedEvent implements IEvent {
    private Classifier classifier;
    private String ep;
    private long time;

    public PatternMatchedEvent(String ep, Classifier classifier) {
        this.classifier = classifier;
        this.ep = ep;
        this.time = System.nanoTime();
    }

    public String getEp() {
        return ep;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
