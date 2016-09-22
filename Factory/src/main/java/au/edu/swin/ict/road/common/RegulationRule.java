package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class RegulationRule {

    private String id;
    private RegRuleManagementState mgtState;

    public RegulationRule(String id, String state) {
        this.id = id;
        this.mgtState = new RegRuleManagementState(id);
        this.mgtState.setState(state);
    }

    public RegulationRule(String id) {
        this.id = id;
        this.mgtState = new RegRuleManagementState(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RegRuleManagementState getMgtState() {
        return mgtState;
    }
}
