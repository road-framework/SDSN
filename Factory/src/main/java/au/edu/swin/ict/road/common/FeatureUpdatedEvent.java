package au.edu.swin.ict.road.common;

public class FeatureUpdatedEvent {
    private String name;
    private String vsnId;

    public FeatureUpdatedEvent(String name, String vsnId) {
        this.name = name;
        this.vsnId = vsnId;
    }

    public String getVsnId() {
        return vsnId;
    }
}
