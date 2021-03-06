package au.edu.swin.ict.road.common;

public class FeatureSelectedEvent implements IEvent {
    private String name;
    private String vsnId;

    public FeatureSelectedEvent(String name, String vsnId) {
        this.name = name;
        this.vsnId = vsnId;
    }

    public String getVsnId() {
        return vsnId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVsnId(String vsnId) {
        this.vsnId = vsnId;
    }
}
