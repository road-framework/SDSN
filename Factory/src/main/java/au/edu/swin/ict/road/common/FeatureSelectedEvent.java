package au.edu.swin.ict.road.common;

public class FeatureSelectedEvent {
    private String name;
    private String vsnId;

    public FeatureSelectedEvent(String name, String vsnId) {
        this.name = name;
        this.vsnId = vsnId;
    }

    public String getVsnId() {
        return vsnId;
    }
}
