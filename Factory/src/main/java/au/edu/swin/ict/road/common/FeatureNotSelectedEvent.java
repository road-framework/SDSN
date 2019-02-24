package au.edu.swin.ict.road.common;

public class FeatureNotSelectedEvent {
    private String name;
    private String vsnId;

    public FeatureNotSelectedEvent(String name, String vsnId) {
        this.name = name;
        this.vsnId = vsnId;
    }

    public String getVsnId() {
        return vsnId;
    }
}
