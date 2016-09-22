package au.edu.swin.ict.road.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * TODO
 */
public abstract class BaseManagedElement extends BaseManagementStateChangeListener {

    private ManagementState mgtState;
    private List<String> excludeVSNInstanceList = new ArrayList<String>();
    private List<String> includeVSNInstanceList = new ArrayList<String>();
    private List<String> versions = new ArrayList<String>();

    public BaseManagedElement(ManagementState state) {
        this.mgtState = state;
    }

    public void includeVSInstance(String vsnInstanceId) {
        includeVSNInstanceList.add(vsnInstanceId);
    }

    public void includeAllVSInstances(Collection<String> vsnInstanceIds) {
        includeVSNInstanceList.addAll(vsnInstanceIds);
    }

    public void reIncludeAllVSInstances(Collection<String> vsnInstanceIds) {
        includeVSNInstanceList.clear();
        includeVSNInstanceList.addAll(vsnInstanceIds);
    }

    public boolean isIncluded(String vsnInstanceId) {
        return includeVSNInstanceList.contains(vsnInstanceId);
    }

    public void removeInclusion(String vsnInstanceId) {
        includeVSNInstanceList.remove(vsnInstanceId);
    }

    public void removeAllInclusions(Collection<String> vsnInstanceIds) {
        includeVSNInstanceList.removeAll(vsnInstanceIds);
    }

    public List<String> getIncludedVsnInstances() {
        return includeVSNInstanceList;
    }

    public void excludeVSInstance(String vsnInstanceId) {
        excludeVSNInstanceList.add(vsnInstanceId);
    }

    public void excludeAllVSInstances(Collection<String> vsnInstanceIds) {
        excludeVSNInstanceList.addAll(vsnInstanceIds);
    }

    public boolean isExcluded(String vsnInstanceId) {
        if (excludeVSNInstanceList.isEmpty()) {
            return false;
        }
        return excludeVSNInstanceList.contains(vsnInstanceId);
    }

    public void removeExclusion(String vsnInstanceId) {
        excludeVSNInstanceList.remove(vsnInstanceId);
    }

    public void removeAllExclusions(Collection<String> vsnInstanceIds) {
        excludeVSNInstanceList.removeAll(vsnInstanceIds);
    }

    public List<String> getExcludedVsnInstances() {
        return excludeVSNInstanceList;
    }

    public ManagementState getMgtState() {
        return mgtState;
    }

    public void addProcessVersion(String version) {
        versions.add(version);
    }

    public boolean containVersion(String version) {
        return versions.contains(version);
    }

    public void removeVersion(String version) {
        versions.remove(version);
    }

    public void notify(ManagementState managementState) {
        if (managementState instanceof VSNInstance) {
            VSNInstance vsnInstance = (VSNInstance) managementState;
            if (vsnInstance.getState().equals(ManagementState.STATE_QUIESCENCE)) {
                removeExclusion(vsnInstance.getClassifier().getProcessInsId());
                removeInclusion(vsnInstance.getClassifier().getProcessInsId());
                if (getMgtState().getState().equals(ManagementState.STATE_PASSIVE)) {
                    if (includeVSNInstanceList.isEmpty()) {
                        getMgtState().setState(ManagementState.STATE_QUIESCENCE);
                    }
                }
            }
        }
    }
}
