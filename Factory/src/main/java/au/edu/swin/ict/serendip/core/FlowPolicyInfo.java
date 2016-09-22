package au.edu.swin.ict.serendip.core;

/**
 * TODO documentation
 */

public class FlowPolicyInfo {

    private String policyKey;
    private String roleId;

    public FlowPolicyInfo(String policyKey, String roleId) {
        this.policyKey = policyKey;
        this.roleId = roleId;
    }

    public String getPolicyKey() {
        return policyKey;
    }

    public String getRoleId() {
        return roleId;
    }
}
