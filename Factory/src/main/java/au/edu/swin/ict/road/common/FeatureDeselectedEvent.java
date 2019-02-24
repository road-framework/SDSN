package au.edu.swin.ict.road.common;

public class FeatureDeselectedEvent {
    private String name;
    private String vsnId;

    public FeatureDeselectedEvent(String name, String vsnId) {
        this.name = name;
        this.vsnId = vsnId;
    }

    public String getVsnId() {
        return vsnId;
    }
}
