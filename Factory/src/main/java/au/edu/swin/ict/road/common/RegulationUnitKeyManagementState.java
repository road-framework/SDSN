package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class RegulationUnitKeyManagementState extends ManagementState {
    private String vsnId;
    private String processId;

    public RegulationUnitKeyManagementState(String id, String state) {
        super(id, state);
    }

    public RegulationUnitKeyManagementState(String id, String state, String vsnId) {
        super(id, state);
        this.vsnId = vsnId;
    }

    public RegulationUnitKeyManagementState(String id, String state, String vsnId, String processId) {
        super(id, state);
        this.vsnId = vsnId;
        this.processId = processId;
    }

    public String getVsnId() {
        return vsnId;
    }

    public void setVsnId(String vsnId) {
        this.vsnId = vsnId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }
}
