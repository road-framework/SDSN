package au.edu.swin.ict.road.regulator;

import au.edu.swin.ict.road.common.Classifier;
import au.edu.swin.ict.serendip.core.SerendipException;
import au.edu.swin.ict.serendip.event.SerendipEventListener;

import java.util.List;

/**
 * TODO documentation   Temporary class due to behaviour of event notification - need to use different classifiers
 */

public class ProcessMonitorInstance extends SerendipEventListener {

    private ProcessMonitor processMonitor;

    public ProcessMonitorInstance(ProcessMonitor processMonitor) {
        this.processMonitor = processMonitor;
    }

    @Override
    public void eventPatternMatched(String ep, Classifier classifier) throws SerendipException {
        processMonitor.eventPatternMatched(ep, classifier);
    }

    @Override
    public String getId() {
        return processMonitor.getId();
    }

    public List<String> getEventPatterns() {
        return processMonitor.getEventPatterns();
    }

    public void setEventPatterns(List<String> eventPatterns) {
        this.processMonitor.setEventPatterns(eventPatterns);
    }

    public void addEventPattern(String eventPattern) {
        this.processMonitor.addEventPattern(eventPattern.trim());
    }
}
