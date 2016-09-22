package au.edu.swin.ict.road.composite.routing;

import au.edu.swin.ict.road.composite.rules.events.composite.RoleServiceMessage;
import au.edu.swin.ict.road.regulator.FactObject;

/**
 * TODO documentation
 */
public class Target {

    private FactObject committedCap;
    private int threshold;
    private String path;

    public FactObject getCommittedCap() {
        return committedCap;
    }

    public void setCommittedCap(FactObject committedCap) {
        this.committedCap = committedCap;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void execute(RoleServiceMessage event) {
        if (Integer.parseInt((String) committedCap.getAttribute("Committed")) <= threshold) {
            event.getMessageWrapper().getClassifier().setProcessId(path);
        } else {
            event.setBlocked(true);
        }
    }
}
