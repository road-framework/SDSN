package au.edu.swin.ict.road.testing;

import au.edu.swin.ict.road.composite.Composite;

/**
 * TODO
 */

public class PolicyExecutor implements Runnable {
    private Composite composite;
    private String policyId;
    private String policyFile;
    private boolean isOrganizational;

    public PolicyExecutor(Composite composite, String policyId, String policyFile, boolean isOrganizational) {
        this.composite = composite;
        this.policyId = policyId;
        this.policyFile = policyFile;
        this.isOrganizational = isOrganizational;
    }

    @Override
    public void run() {
        if (isOrganizational) {
            composite.getOrganiserRole().enactOrganizationalManagementPolicy(policyId, policyFile);
        } else {
            composite.getOperationalManagerRole().enactOperationalManagementPolicy(policyId, policyFile);
        }
    }
}
