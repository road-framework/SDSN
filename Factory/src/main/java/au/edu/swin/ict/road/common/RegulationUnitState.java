package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class RegulationUnitState {
    private String id;
    public static String STATE_ACTIVE = "active";
    public static String STATE_PASSIVE = "passive";
    public static String STATE_QUIESCENCE = "quiescence";
    private String state = STATE_PASSIVE;

    public RegulationUnitState(String id) {
        this.id = id;
    }

    public RegulationUnitState(String id, String state) {
        this.id = id;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
