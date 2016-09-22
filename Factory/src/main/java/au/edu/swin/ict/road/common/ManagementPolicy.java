package au.edu.swin.ict.road.common;

/**
 * TODO
 */
public class ManagementPolicy {
    private String policyId;
    private DroolsManagementRules managementRules;
    private ManagementPolicyState managementPolicyState;

    public ManagementPolicy(String policyId, DroolsManagementRules managementRules, ManagementPolicyState managementPolicyState) {
        this.policyId = policyId;
        this.managementRules = managementRules;
        this.managementPolicyState = managementPolicyState;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
    }

    public DroolsManagementRules getManagementRules() {
        return managementRules;
    }

    public void setManagementRules(DroolsManagementRules managementRules) {
        this.managementRules = managementRules;
    }

    public ManagementPolicyState getManagementPolicyState() {
        return managementPolicyState;
    }

    public void setManagementPolicyState(ManagementPolicyState managementPolicyState) {
        this.managementPolicyState = managementPolicyState;
    }
}
