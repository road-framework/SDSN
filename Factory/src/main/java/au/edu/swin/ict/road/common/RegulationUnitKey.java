package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class RegulationUnitKey extends BaseManagedElement {
    private String unitId;

    public RegulationUnitKey(ManagementState state, String unitId) {
        super(state);
        this.unitId = unitId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
