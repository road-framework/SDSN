package au.edu.swin.ict.road.composite.routing;

/**
 * TODO documentation
 */
public class Route {
    private int threshold;
    private String processId;
    private int weight = 1;

    public Route(String processId) {
        this.processId = processId;
    }

    public Route(String processId, int weight) {
        this.processId = processId;
        this.weight = weight;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }
}
