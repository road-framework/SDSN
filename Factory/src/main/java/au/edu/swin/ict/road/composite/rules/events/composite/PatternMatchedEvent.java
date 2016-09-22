package au.edu.swin.ict.road.composite.rules.events.composite;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.road.common.IEvent;

/**
 * TODO documentation
 */
public class PatternMatchedEvent implements IEvent {
    private Classifier classifier;

    public String getEp() {
        return ep;
    }

    public PatternMatchedEvent(String ep, Classifier classifier) {
        this.classifier = classifier;
        this.ep = ep;
        this.time = System.nanoTime();
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public void setClassifier(Classifier classifier) {
        this.classifier = classifier;
    }

    private String ep;
    private long time;

    public Classifier getClassifier() {
        return classifier;
    }

    public void setTime(long time) {
        this.time = time;
    }


    public long getTime() {
        return time;
    }
}
